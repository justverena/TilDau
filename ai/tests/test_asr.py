from asr.whisper_asr import transcribe_audio

audio_path = "tests/sample.wav"

text = transcribe_audio(audio_path)
print("Recognized:", text)