def generate_feedback(flags: list[str]) -> list[str]:
    feedback = []

    for f in flags:
        if f == "too_fast_tempo":
            feedback.append("Try speaking a bit slower / Сәл баяу сөйлеңіз")
        elif f == "many_long_pauses":
            feedback.append("Try to reduce long pauses / Ұзақ үзілістерді азайтыңыз")
        elif f == "long_average_pause":
            feedback.append("Try to speak more smoothly / Сөйлеуді біртекті жасаңыз")
        elif f == "minor_pronunciation_errors":
            feedback.append("Check your pronunciation of some words / Кейбір сөздерді дұрыс айтуды тексеріңіз")
        elif f == "moderate_pronunciation_errors":
            feedback.append("Pay attention to pronunciation / Дауыс шығаруға назар аударыңыз")
        elif f == "severe_pronunciation_errors":
            feedback.append("Practice pronunciation carefully / Дауыс шығару жаттығуын мұқият жасаңыз")
        elif f == "word_count_mismatch":
            feedback.append("Some words may be missing or extra / Кейбір сөздер жетіспеуі немесе артық болуы мүмкін")

    return feedback