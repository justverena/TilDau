import librosa
import numpy as np

def detect_pauses(audio, sr, threshold_db=-35, min_silence_sec=0.25):
    # threshold is a quiet level, min silence is a pause minimal duration
    intervals = librosa.effects.split(audio, top_db=-threshold_db)
    pauses = []
    if len(intervals) == 0:
        return[len(audio)/sr]
    
    if intervals[0][0]> 0:
        pauses.append(intervals[0][0]/sr)

    for i in range(len(intervals)-1):
        pause_len = (intervals[i+1][0] - intervals[i][1]) / sr
        if pause_len >= min_silence_sec:
            pauses.append(pause_len)
    
    if intervals[-1][1] < len(audio):
        pauses.append((len(audio) - intervals[-1][1]/sr))

    return pauses

def compute_tempo(text, duration):
    vowels = "аәеэёоөұүыіуияюeaiouy"
    syllables = sum([1 for c in text.lower() if c in vowels])
    if duration <= 0:
        return 0
    
    return syllables / duration