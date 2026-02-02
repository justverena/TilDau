import numpy as np
    
def normalize_audio(audio, eps=1e-6):
    rms = np.sqrt(np.mean(audio**2) + eps)
    target_rms = 0.03
    return audio * (target_rms / rms)