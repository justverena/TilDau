import librosa
import numpy as np


def trim_silence(audio, sr, top_db=25):
    trimmed, _ = librosa.effects.trim(audio, top_db=top_db)
    return trimmed


def compute_basic_features(audio, trimmed_audio, sr):
    duration = len(audio) / sr
    voiced_duration = len(trimmed_audio) / sr

    silence_ratio = max(
        0.0,
        1.0 - voiced_duration / (duration + 1e-6)
    )

    return {
        "duration": round(duration, 3),
        "voiced_duration": round(voiced_duration, 3),
        "silence_ratio": round(silence_ratio, 3)
    }