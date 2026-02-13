from fluency.tempo_utils import detect_pauses, compute_tempo
def compute_fluency_metrics(audio, sr, text):
    duration = len(audio) / sr
    pauses = detect_pauses(audio, sr)
    tempo = compute_tempo(text, duration)

    long_pause_threshold = 0.7

    long_pauses = [p for p in pauses if p >= long_pause_threshold]

    total_pause_time = sum(pauses)
    silence_ratio = total_pause_time / (duration + 1e-6)

    metrics = {
        "duration_sec": round(duration, 2),
        "tempo_syllables_per_sec": round(tempo, 2),
        "avg_pause_sec": round(sum(pauses) / len(pauses), 2) if pauses else 0.0,
        "max_pause_sec": round(max(pauses), 2) if pauses else 0.0,
        "long_pauses_count": len(long_pauses),
        "total_pauses_count": len(pauses),
        "silence_ratio": round(silence_ratio, 3),
    }

    return metrics