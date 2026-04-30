def generate_feedback(flags: list[str]) -> list[str]:
    feedback = []

    for f in flags:
        if f == "too_fast_tempo":
            feedback.append("Сәл баяу сөйлеңіз")
        elif f == "too_slow_or_no_speech":
            feedback.append("Сәл тезірек сөйлеңіз")
        elif f == "too_much_silence":
            feedback.append("Сәл қаттырақ сөйлеңіз")
        elif f == "many_long_pauses":
            feedback.append("Ұзақ үзілістерді азайтыңыз")
        elif f == "long_average_pause":
            feedback.append("Сөйлеуді біртекті жасаңыз")
        elif f == "minor_pronunciation_errors":
            feedback.append("Кейбір сөздерді дұрыс айтуды тексеріңіз")
        elif f == "moderate_pronunciation_errors":
            feedback.append("Дауыс шығаруға назар аударыңыз")
        elif f == "severe_pronunciation_errors":
            feedback.append("Дауыс шығару жаттығуын мұқият жасаңыз")
        elif f == "word_count_mismatch":
            feedback.append("Кейбір дыбыстар жетіспеуі немесе артық болуы мүмкін")
        elif f == "low_speech_quality":
            feedback.append("Сіздің сөйлеуіңіз үлгіден айтарлықтай ерекшеленеді")
        elif f == "unstable_pronunciation":
            feedback.append("Сөйлеуіңізде тұрақсыздық байқалады")
    return feedback