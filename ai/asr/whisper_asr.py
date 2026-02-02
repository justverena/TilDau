import whisper

_model: whisper.Whisper = whisper.load_model("small")

def transcribe_audio(audio_path: str, language: str ="kk") -> str:
    result: dict = _model.transcribe(
        audio_path,
        language=language,
        fp16=False
    )
    text: str = result.get("text", "")
    return text.strip()