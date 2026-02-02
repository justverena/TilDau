import re
from jiwer import wer

def normalize_text(text):
    text = text.lower()
    text = re.sub(r"[^\w\sәіқңғүұө]", "", text)
    text = re.sub(r"\s+", " ", text)
    return text.strip()

def compute_asr_metrics(expected_text, recognized_text):
    expected = normalize_text(expected_text)
    recognized = normalize_text(recognized_text)

    wer_value = wer(expected, recognized)

    expected_words = expected.split()
    recognized_words = recognized.split()

    metrics = {
        "wer": round(wer_value, 3),
        "expected_word_count": len(expected_words),
        "recognized_word_count": len(recognized_words),
        "word_count_diff": len(recognized_words) - len(expected_words)
    }

    return metrics