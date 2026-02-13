from asr.whisper_asr import transcribe_audio

path = "tests/sample.wav"
text = transcribe_audio(path)

print(text)
