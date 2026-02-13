import numpy as np

def score_embedding(similarity: float) -> dict:
    if np.isnan(similarity):
        similarity = 0.0

    score = int(max(0, min(100, similarity * 100)))

    flags = []
    if similarity < 0.85:
        score -= 5
        flags.append("low_speech_quality")
    elif similarity < 0.9:
        flags.append("unstable_pronunciation")

    return {
        "embedding_score": score,
        "similarity": round(similarity, 3),
        "flags": flags
    }