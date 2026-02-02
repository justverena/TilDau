from asr.asr_metrics import compute_asr_metrics

expected = "Жазға жетсең, қыс күніңді ұмытпа"
recognized = "Жазла жетсен қоскүнімді мұтпа."

metrics = compute_asr_metrics(expected, recognized)
print(metrics)

# "Жаман адамға іс түссе, жаныңды сұрар шешуге."