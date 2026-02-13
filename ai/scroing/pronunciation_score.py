def smooth_wer(wer_value: float, method: str = "linear") -> float:

    if method == "linear":
        if wer_value >= 1.0:
            return 0.8
        elif wer_value > 0.8:
            return 0.8 + 0.3 * (wer_value - 0.8)
        else:
            return wer_value
    elif method == "sqrt":
        return min(1.0, wer_value ** 0.5)
    else:
        return wer_value

def score_pronunciation(metrics: dict) -> dict:
    score = 100
    flags = []

    wer = metrics.get("wer", 0.0)
    smoothed_wer = smooth_wer(wer)

    if smoothed_wer <= 0.5:
        penalty = 0
    elif smoothed_wer <= 0.7:
        penalty = 10
        flags.append("minor_pronunciation_errors")
    elif smoothed_wer <= 0.9:
        penalty = 25
        flags.append("moderate_pronunciation_errors")
    else:
        penalty = 40
        flags.append("severe_pronunciation_errors")

    score -= penalty

    word_diff = metrics.get("word_count_diff", 0)
    if abs(word_diff) >= 4:
        score -= 5
        flags.append("word_count_mismatch")

    score = max(0, min(100, score))

    return {
        "pronunciation_score": score,
        "flags": flags
    }