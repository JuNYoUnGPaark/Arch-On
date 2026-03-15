import numpy as np
from dataclasses import dataclass


@dataclass
class RuleConfig:
    fs = 50.0
    window_sec = 2.0
    eps = 1e-8
    arch_ratio_good = 1.10
    arch_ratio_fair = 1.03
    arch_ratio_poor = 0.98
    emg_ratio_good = 1.50
    emg_ratio_fair = 1.20
    emg_ratio_poor = 1.00
    pron_delta_good = 2.0
    pron_delta_fair = 5.0
    pron_delta_poor = 8.0
    pron_std_warn = 3.0
    emg_low_warn = 1.10
    pron_excess_warn = 5.0
    w_pressure = 0.40
    w_emg = 0.35
    w_posture = 0.25


def moving_average(x, k):
    if k <= 1:
        return x.copy()
    pad = k // 2
    x_pad = np.pad(x, (pad, pad), mode="edge")
    kernel = np.ones(k) / k
    return np.convolve(x_pad, kernel, mode="valid")


def rms_1d(x):
    return float(np.sqrt(np.mean(np.square(x))))


def safe_div(a, b, eps=1e-8):
    return float(a / (b + eps))


def windowed_view(x, window_size, step_size):
    out = []
    n = len(x)
    for s in range(0, n - window_size + 1, step_size):
        out.append(x[s:s + window_size])
    return out


def estimate_pronation_angle_from_imu(acc, gyro, fs, alpha=0.98):
    T = acc.shape[0]
    dt = 1.0 / fs

    acc_x = acc[:, 0]
    acc_y = acc[:, 1]
    acc_z = acc[:, 2]

    acc_angle = np.degrees(np.arctan2(acc_y, np.sqrt(acc_x**2 + acc_z**2) + 1e-8))

    gyro_x = gyro[:, 0]
    angle = np.zeros(T, dtype=np.float64)
    angle[0] = acc_angle[0]

    for t in range(1, T):
        gyro_pred = angle[t - 1] + gyro_x[t] * dt
        angle[t] = alpha * gyro_pred + (1 - alpha) * acc_angle[t]

    return angle


def extract_fsr_features(fsr, eps=1e-8):
    fore = fsr[:, 0]
    mid = fsr[:, 1]
    rear = fsr[:, 2]

    fore_mean = float(np.mean(fore))
    mid_mean = float(np.mean(mid))
    rear_mean = float(np.mean(rear))
    total_mean = fore_mean + mid_mean + rear_mean

    fore_ratio = safe_div(fore_mean, total_mean, eps)
    mid_ratio = safe_div(mid_mean, total_mean, eps)
    rear_ratio = safe_div(rear_mean, total_mean, eps)

    arch_proxy = safe_div((fore_mean + rear_mean), mid_mean, eps)

    return {
        "fore_mean": fore_mean,
        "mid_mean": mid_mean,
        "rear_mean": rear_mean,
        "fore_ratio": fore_ratio,
        "mid_ratio": mid_ratio,
        "rear_ratio": rear_ratio,
        "arch_proxy": arch_proxy,
    }


def extract_emg_features(emg, smooth_k=5):
    emg = emg.astype(np.float64)
    emg_rect = np.abs(emg)
    emg_env = moving_average(emg_rect, smooth_k)

    emg_rms = rms_1d(emg)
    emg_mav = float(np.mean(emg_rect))
    emg_env_mean = float(np.mean(emg_env))
    emg_env_std = float(np.std(emg_env))

    return {
        "emg_rms": emg_rms,
        "emg_mav": emg_mav,
        "emg_env_mean": emg_env_mean,
        "emg_env_std": emg_env_std,
    }


def extract_imu_features(pronation_angle):
    return {
        "pron_mean": float(np.mean(pronation_angle)),
        "pron_std": float(np.std(pronation_angle)),
        "pron_max": float(np.max(pronation_angle)),
        "pron_min": float(np.min(pronation_angle)),
        "pron_range": float(np.max(pronation_angle) - np.min(pronation_angle)),
    }


def extract_all_features(fsr, emg, acc, gyro, config):
    pron = estimate_pronation_angle_from_imu(acc, gyro, fs=config.fs)

    fsr_feat = extract_fsr_features(fsr, eps=config.eps)
    emg_feat = extract_emg_features(emg)
    imu_feat = extract_imu_features(pron)

    out = {}
    out.update(fsr_feat)
    out.update(emg_feat)
    out.update(imu_feat)
    return out


def compute_relative_metrics(baseline_feat, current_feat, eps=1e-8):
    arch_change_ratio = safe_div(current_feat["arch_proxy"], baseline_feat["arch_proxy"], eps)
    emg_ratio = safe_div(current_feat["emg_rms"], baseline_feat["emg_rms"], eps)
    delta_pronation = current_feat["pron_mean"] - baseline_feat["pron_mean"]
    pron_std = current_feat["pron_std"]

    return {
        "arch_change_ratio": arch_change_ratio,
        "emg_ratio": emg_ratio,
        "delta_pronation": delta_pronation,
        "pron_std": pron_std,
    }


def score_pressure(arch_change_ratio, cfg):
    if arch_change_ratio >= cfg.arch_ratio_good:
        return 100
    elif arch_change_ratio >= cfg.arch_ratio_fair:
        return 70
    elif arch_change_ratio >= cfg.arch_ratio_poor:
        return 40
    return 10


def score_emg(emg_ratio, cfg):
    if emg_ratio >= cfg.emg_ratio_good:
        return 100
    elif emg_ratio >= cfg.emg_ratio_fair:
        return 70
    elif emg_ratio >= cfg.emg_ratio_poor:
        return 40
    return 10


def score_posture(delta_pronation, cfg):
    if delta_pronation <= cfg.pron_delta_good:
        return 100
    elif delta_pronation <= cfg.pron_delta_fair:
        return 70
    elif delta_pronation <= cfg.pron_delta_poor:
        return 40
    return 10


def detect_compensation(emg_ratio, delta_pronation, pron_std, cfg):
    flags = []

    if emg_ratio < cfg.emg_low_warn:
        flags.append("low_target_emg_activation")

    if delta_pronation > cfg.pron_excess_warn:
        flags.append("excessive_pronation")

    if pron_std > cfg.pron_std_warn:
        flags.append("unstable_posture")

    return flags


def generate_feedback(pressure_score, emg_score, posture_score, flags):
    msg = []

    if pressure_score < 50:
        msg.append("Comment1")

    if emg_score < 50:
        msg.append("Comment2")

    if "excessive_pronation" in flags:
        msg.append("Comment3")

    if "unstable_posture" in flags:
        msg.append("Comment4")

    if not msg:
        msg.append("Comment5")

    return msg


def decide_label(final_score, flags):
    if "excessive_pronation" in flags:
        return "Compensation"
    if final_score >= 80:
        return "Good"
    elif final_score >= 60:
        return "Fair"
    return "Poor"


class ArchOnRuleEvaluator:
    def __init__(self, config):
        self.cfg = config

    def extract_features_from_raw(self, fsr, emg, acc, gyro):
        return extract_all_features(fsr, emg, acc, gyro, self.cfg)

    def evaluate_single_segment(self, baseline_raw, exercise_raw):
        baseline_feat = self.extract_features_from_raw(
            baseline_raw["fsr"],
            baseline_raw["emg"],
            baseline_raw["acc"],
            baseline_raw["gyro"],
        )
        exercise_feat = self.extract_features_from_raw(
            exercise_raw["fsr"],
            exercise_raw["emg"],
            exercise_raw["acc"],
            exercise_raw["gyro"],
        )

        rel = compute_relative_metrics(baseline_feat, exercise_feat, self.cfg.eps)

        pressure_score = score_pressure(rel["arch_change_ratio"], self.cfg)
        emg_score = score_emg(rel["emg_ratio"], self.cfg)
        posture_score = score_posture(rel["delta_pronation"], self.cfg)

        final_score = (
            self.cfg.w_pressure * pressure_score +
            self.cfg.w_emg * emg_score +
            self.cfg.w_posture * posture_score
        )

        flags = detect_compensation(
            rel["emg_ratio"],
            rel["delta_pronation"],
            rel["pron_std"],
            self.cfg
        )

        label = decide_label(final_score, flags)
        feedback = generate_feedback(pressure_score, emg_score, posture_score, flags)

        return {
            "baseline_features": baseline_feat,
            "exercise_features": exercise_feat,
            "relative_metrics": rel,
            "scores": {
                "pressure_score": pressure_score,
                "emg_score": emg_score,
                "posture_score": posture_score,
                "final_score": round(float(final_score), 2),
            },
            "flags": flags,
            "label": label,
            "feedback": feedback,
        }

    def evaluate_windowed_session(self, baseline_raw, exercise_raw, step_sec=1.0):
        baseline_feat = self.extract_features_from_raw(
            baseline_raw["fsr"], baseline_raw["emg"], baseline_raw["acc"], baseline_raw["gyro"]
        )

        w = int(self.cfg.window_sec * self.cfg.fs)
        s = int(step_sec * self.cfg.fs)

        T = exercise_raw["fsr"].shape[0]
        if T < w:
            raise ValueError("exercise_raw length is shorter than one analysis window.")

        idx_ranges = [(i, i + w) for i in range(0, T - w + 1, s)]
        outputs = []

        for st, ed in idx_ranges:
            cur_feat = self.extract_features_from_raw(
                exercise_raw["fsr"][st:ed],
                exercise_raw["emg"][st:ed],
                exercise_raw["acc"][st:ed],
                exercise_raw["gyro"][st:ed],
            )

            rel = compute_relative_metrics(baseline_feat, cur_feat, self.cfg.eps)

            pressure_score = score_pressure(rel["arch_change_ratio"], self.cfg)
            emg_score = score_emg(rel["emg_ratio"], self.cfg)
            posture_score = score_posture(rel["delta_pronation"], self.cfg)

            final_score = (
                self.cfg.w_pressure * pressure_score +
                self.cfg.w_emg * emg_score +
                self.cfg.w_posture * posture_score
            )

            flags = detect_compensation(
                rel["emg_ratio"],
                rel["delta_pronation"],
                rel["pron_std"],
                self.cfg
            )

            label = decide_label(final_score, flags)
            feedback = generate_feedback(pressure_score, emg_score, posture_score, flags)

            outputs.append({
                "start_idx": st,
                "end_idx": ed,
                "relative_metrics": rel,
                "scores": {
                    "pressure_score": pressure_score,
                    "emg_score": emg_score,
                    "posture_score": posture_score,
                    "final_score": round(float(final_score), 2),
                },
                "flags": flags,
                "label": label,
                "feedback": feedback,
            })

        final_scores = [o["scores"]["final_score"] for o in outputs]
        labels = [o["label"] for o in outputs]

        session_summary = {
            "mean_final_score": float(np.mean(final_scores)),
            "min_final_score": float(np.min(final_scores)),
            "max_final_score": float(np.max(final_scores)),
            "label_histogram": {lab: labels.count(lab) for lab in sorted(set(labels))},
        }

        return {
            "baseline_features": baseline_feat,
            "window_outputs": outputs,
            "session_summary": session_summary,
        }


def make_dummy_baseline(T, seed=42):
    rng = np.random.default_rng(seed)

    fore = 30 + 2.0 * rng.normal(size=T)
    mid = 25 + 2.0 * rng.normal(size=T)
    rear = 35 + 2.0 * rng.normal(size=T)
    fsr = np.stack([fore, mid, rear], axis=1)

    emg = 0.02 * rng.normal(size=T)

    acc_x = 0.02 * rng.normal(size=T)
    acc_y = 0.03 * rng.normal(size=T)
    acc_z = 1.0 + 0.02 * rng.normal(size=T)
    acc = np.stack([acc_x, acc_y, acc_z], axis=1)

    gyro = 0.5 * rng.normal(size=(T, 3))

    return {"fsr": fsr, "emg": emg, "acc": acc, "gyro": gyro}


def make_dummy_exercise_good(T, seed=7):
    rng = np.random.default_rng(seed)

    fore = 33 + 2.0 * rng.normal(size=T)
    mid = 19 + 1.5 * rng.normal(size=T)
    rear = 39 + 2.0 * rng.normal(size=T)
    fsr = np.stack([fore, mid, rear], axis=1)

    emg = 0.06 * rng.normal(size=T) + 0.03 * np.sin(np.linspace(0, 8 * np.pi, T))

    acc_x = 0.02 * rng.normal(size=T)
    acc_y = 0.02 * rng.normal(size=T) + 0.03
    acc_z = 1.0 + 0.02 * rng.normal(size=T)
    acc = np.stack([acc_x, acc_y, acc_z], axis=1)

    gyro = 0.7 * rng.normal(size=(T, 3))
    gyro[:, 0] += 0.3

    return {"fsr": fsr, "emg": emg, "acc": acc, "gyro": gyro}


def make_dummy_exercise_bad_comp(T, seed=99):
    rng = np.random.default_rng(seed)

    fore = 29 + 2.5 * rng.normal(size=T)
    mid = 31 + 2.5 * rng.normal(size=T)
    rear = 33 + 2.5 * rng.normal(size=T)
    fsr = np.stack([fore, mid, rear], axis=1)

    emg = 0.025 * rng.normal(size=T)

    acc_x = 0.03 * rng.normal(size=T)
    acc_y = 0.03 * rng.normal(size=T) + 0.20
    acc_z = 1.0 + 0.03 * rng.normal(size=T)
    acc = np.stack([acc_x, acc_y, acc_z], axis=1)

    gyro = 1.5 * rng.normal(size=(T, 3))
    gyro[:, 0] += 8.0

    return {"fsr": fsr, "emg": emg, "acc": acc, "gyro": gyro}


if __name__ == "__main__":
    cfg = RuleConfig()
    evaluator = ArchOnRuleEvaluator(cfg)

    T_baseline = 300
    T_ex = 500

    baseline = make_dummy_baseline(T_baseline)
    ex_good = make_dummy_exercise_good(T_ex)
    ex_bad = make_dummy_exercise_bad_comp(T_ex)

    print("=" * 70)
    print("Single segment evaluation: GOOD case")
    result_good = evaluator.evaluate_single_segment(baseline, ex_good)
    print("label:", result_good["label"])
    print("scores:", result_good["scores"])
    print("relative:", result_good["relative_metrics"])
    print("flags:", result_good["flags"])
    print("feedback:", result_good["feedback"])

    print("\n" + "=" * 70)
    print("Single segment evaluation: BAD / COMPENSATION case")
    result_bad = evaluator.evaluate_single_segment(baseline, ex_bad)
    print("label:", result_bad["label"])
    print("scores:", result_bad["scores"])
    print("relative:", result_bad["relative_metrics"])
    print("flags:", result_bad["flags"])
    print("feedback:", result_bad["feedback"])

    print("\n" + "=" * 70)
    print("Windowed session evaluation: GOOD case")
    session_good = evaluator.evaluate_windowed_session(baseline, ex_good, step_sec=1.0)
    print("session_summary:", session_good["session_summary"])

    print("\n" + "=" * 70)
    print("Windowed session evaluation: BAD / COMPENSATION case")
    session_bad = evaluator.evaluate_windowed_session(baseline, ex_bad, step_sec=1.0)
    print("session_summary:", session_bad["session_summary"])

    

"""
======================================================================
Single segment evaluation: GOOD case
label: Good
scores: {'pressure_score': 100, 'emg_score': 100, 'posture_score': 70, 'final_score': 92.5}
relative: {'arch_change_ratio': 1.457321900167206, 'emg_ratio': 3.272574262386499, 'delta_pronation': 2.3151031838452116, 'pron_std': 0.1652237290527576}
flags: []
feedback: ['Comment5']

======================================================================
Single segment evaluation: BAD / COMPENSATION case
label: Compensation
scores: {'pressure_score': 10, 'emg_score': 70, 'posture_score': 10, 'final_score': 31.0}
relative: {'arch_change_ratio': 0.7737902699418124, 'emg_ratio': 1.2711117746314258, 'delta_pronation': 18.771950262231627, 'pron_std': 1.7188287070605703}
flags: ['excessive_pronation']
feedback: ['Comment1', 'Comment3']

======================================================================
Windowed session evaluation: GOOD case
session_summary: {'mean_final_score': 93.33333333333333, 'min_final_score': 92.5, 'max_final_score': 100.0, 'label_histogram': {'Good': 9}}

======================================================================
Windowed session evaluation: BAD / COMPENSATION case
session_summary: {'mean_final_score': 29.833333333333332, 'min_final_score': 20.5, 'max_final_score': 31.0, 'label_histogram': {'Compensation': 9}}
"""
