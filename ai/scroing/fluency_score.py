
def score_fluency(metrics):
    score = 100
    flags = []
    fast_threshold = 5.0
    many_long_pauses_threshold = 2
    avg_pause_threshold = 0.7
    min_tempo_threshold = 1

    if metrics["tempo_syllables_per_sec"] < min_tempo_threshold:
        score -= 20
        flags.append("too_slow_or_no_speech")

    if metrics.get("silence_ratio", 0) > 0.6:
        score -= 20
        flags.append("too_much_silence")
        
    if metrics["tempo_syllables_per_sec"] > fast_threshold:
        score -= 15
        flags.append("too_fast_tempo")
    
    if metrics["long_pauses_count"] > many_long_pauses_threshold:
        score -= 10
        flags.append("many_long_pauses")

    if metrics["avg_pause_sec"] > avg_pause_threshold:
        score -= 5
        flags.append("long_average_pause")
    
    score = max(0, min(100, score))
    
    return {
        "fluency_score" : score,
        "flags" : flags
    }