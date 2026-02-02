def score_pronunciation(metrics: dict) -> dict:
    score = 100
    flags = []

    wer = metrics.get("wer", 0.0)

    if wer <= 0.1:
        penalty = 0
    elif wer <= 0.2:
        penalty = 10
        flags.append("minor_pronunciation_errors")
    elif wer <= 0.35:
        penalty = 25
        flags.append("moderate_pronunciation_errors")
    else:
        penalty = 40
        flags.append("severe_pronunciation_errors")

    score -= penalty

    word_diff = metrics.get("word_count_diff", 0)
    if abs(word_diff) >= 2:
        score -= 5
        flags.append("word_count_mismatch")

    score = max(0, min(100, score))

    return {
        "pronunciation_score": score,
        "flags": flags
    }