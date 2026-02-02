-- Seed: Тахилалия бойынша негізгі курс

CREATE TEMP TABLE tmp_units (
                                id UUID,
                                title VARCHAR(255),
                                description TEXT,
                                order_index INTEGER
) ON COMMIT PRESERVE ROWS;

WITH new_course AS (
INSERT INTO courses (id, defect_type_id, title, description, is_active)
VALUES (
    uuid_generate_v4(),
    2,
    'Негізгі курс: сөйлеу қарқыны мен реттілігін бақылау',
    'Бұл курс тез әрі үзіліссіз, ретсіз сөйлейтін адамдарға арналған. Курс тұтығудың тырысқақ (судорогалық) түрлеріне арналмаған және логопед немесе дәрігермен кеңесудің орнына қолданылмайды.',
    true
    )
    RETURNING id
    )
INSERT INTO tmp_units (id, title, description, order_index)
SELECT
    uuid_generate_v4(),
    t.title,
    t.description,
    t.order_index
FROM new_course nc
         CROSS JOIN (
    VALUES
        (
            'Сөйлеу қарқынын түсіну және баяулату',
            'Пайдаланушы өзінің сөйлеу қарқынын байқап, оны саналы түрде баяулатуды үйренеді.',
            1
        ),
        (
            'Тыныс алу мен кідірістер – сөйлеу реттілігінің негізі',
            'Фразаларды бір деммен емес, кідірістер арқылы айту дағдысын қалыптастыру.',
            2
        ),
        (
            'Үздіксіз сөйлеуде қарқынды бақылау',
            'Сөйлемнің ортасында жылдамдап кетпей, тұрақты қарқынды сақтау.',
            3
        ),
        (
            'Сөйлеудің реттілігі мен біртұтастығы',
            'Сөздер арасындағы ретсіздігі мен шашыраңқылықты азайту.',
            4
        ),
        (
            'Дағдыны бекіту және күнделікті сөйлеуге көшіру',
            'Үйренген дағдыларды бекітіп, өмірдегі сөйлеуге жақындату.',
            5
        )
) AS t(title, description, order_index);

INSERT INTO course_units (id, course_id, title, description, order_index)
SELECT
    tu.id,
    c.id,
    tu.title,
    tu.description,
    tu.order_index
FROM tmp_units tu
         JOIN courses c
              ON c.title = 'Негізгі курс: сөйлеу қарқыны мен реттілігін бақылау';

INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
SELECT
    uuid_generate_v4(),
    u.id,
    t.exercise_type,
    t.title,
    t.instruction,
    t.expected_text,
    t.reference_audio_url,
    t.order_index
FROM tmp_units u
         CROSS JOIN LATERAL (
    VALUES
        (
            'READ_ALOUD',
            'Сөздерді баяу оқу',
            'Сөздерді асықпай, әр сөздің арасында кідіріс жасап оқыңыз.',
            'Көш жүре түзеледі',
            NULL,
            1
        ),
        (
            'READ_ALOUD',
            'Қысқа мақал-мәтелді оқу',
            'Мақалды бірқалыпты және баяу оқыңыз.',
            'Бала, баланың ісі шала',
            NULL,
            2
        ),
        (
            'REPEAT_AFTER_AUDIO',
            'Диктордан кейін баяу қарқында қайталау',
            'Диктордың сөйлеу қарқынын дәл қайталауға тырысыңыз.',
            'Қызмет қыл да, міндет қыл',
            'exercise-references/stuttering/u1/ex3.wav',
            3
        )
        ) AS t(exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
WHERE u.order_index = 1;

INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
SELECT
    uuid_generate_v4(),
    u.id,
    t.exercise_type,
    t.title,
    t.instruction,
    t.expected_text,
    t.reference_audio_url,
    t.order_index
FROM tmp_units u
         CROSS JOIN LATERAL (
    VALUES
        (
            'READ_ALOUD',
            'Кідірістермен оқу',
            'Әр сызықшадан кейін қысқа кідіріс жасаңыз.',
            'Ер қаруы — бес қару',
            NULL,
            1
        ),
        (
            'REPEAT_AFTER_AUDIO',
            'Кідірістермен бірге қайталап сөйлеу',
            'Диктордан кейін кідірістерді сақтай отырып қайталаңыз.',
            'Байтал түгіл, бас қайғы',
            'exercise-references/stuttering/u2/ex2.wav',
            2
        ),
        (
            'READ_ALOUD',
            'Ұзын мақал-мәтелді оқу',
            'Мәтінді мағыналық бөліктерге бөліп оқыңыз.',
            'Ескісіз жаңа болмас, жамансыз жақсы болмас',
            NULL,
            3
        )
        ) AS t(exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
WHERE u.order_index = 2;

INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
SELECT
    uuid_generate_v4(),
    u.id,
    t.exercise_type,
    t.title,
    t.instruction,
    t.expected_text,
    t.reference_audio_url,
    t.order_index
FROM tmp_units u
         CROSS JOIN LATERAL (
    VALUES
        (
            'READ_ALOUD',
            'Бірқалыпты қарқынмен оқу',
            'Сөйлемнің басында да, соңында да бірдей қарқынды сақтап оқыңыз.',
            'Білімдімен жолдас болсаң, білікті боларсың',
            NULL,
            1
        ),
        (
            'REPEAT_AFTER_AUDIO',
            'Қарқынды сақтай отырып қайталау',
            'Диктордың сөйлеу қарқынын бүкіл сөйлем бойы сақтауға тырысыңыз.',
            'Ақылдының ақылы сарқылмайтын көлмен тең',
            'exercise-references/stuttering/u3/ex2.wav',
            2
        ),
        (
            'READ_ALOUD',
            'Ұзақ сөйлемді қарқынсыз оқымау',
            'Сөйлемнің ортасында жылдамдап кетпей, бірқалыпты оқыңыз.',
            'Ет жегеннің бәрі қонақ, енші алғанның бәрі бөрік',
            NULL,
            3
        )
        ) AS t(exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
WHERE u.order_index = 3;

INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
SELECT
    uuid_generate_v4(),
    u.id,
    t.exercise_type,
    t.title,
    t.instruction,
    t.expected_text,
    t.reference_audio_url,
    t.order_index
FROM tmp_units u
         CROSS JOIN LATERAL (
    VALUES
        (
            'READ_ALOUD',
            'Мағыналық бөліктерге бөліп оқу',
            'Әр мағыналық бөліктен кейін қысқа кідіріс жасап оқыңыз.',
            'Көз сыртында көз бар, тіл астында тіл бар',
            NULL,
            1
        ),
        (
            'REPEAT_AFTER_AUDIO',
            'Ретті сөйлеуді қайталау',
            'Диктордың сөйлеу құрылымын сақтай отырып қайталаңыз.',
            'Аяғын кебіс қыспаған, енесі ернін қыспаған',
            'exercise-references/stuttering/u4/ex2.wav',
            2
        ),
        (
            'READ_ALOUD',
            'Сөздер арасындағы байланысты сақтау',
            'Сөздерді жұтып немесе қосып жібермей, анық оқыңыз.',
            'Жастықта бейнет бер, қартайғанда зейнет бер',
            NULL,
            3
        )
        ) AS t(exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
WHERE u.order_index = 4;

INSERT INTO exercises (id, unit_id, exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
SELECT
    uuid_generate_v4(),
    u.id,
    t.exercise_type,
    t.title,
    t.instruction,
    t.expected_text,
    t.reference_audio_url,
    t.order_index
FROM tmp_units u
         CROSS JOIN LATERAL (
    VALUES
        (
            'READ_ALOUD',
            'Күнделікті сөйлеуге жақын оқу',
            'Мәтінді күнделікті әңгіме сияқты, асықпай оқыңыз.',
            'Қасықтап алғанмен, көл таусылмайды, қанша ұры алғанмен, ел таусылмайды',
            NULL,
            1
        ),
        (
            'READ_ALOUD',
            'Өзіндік бақылаумен оқу',
            'Өз сөйлеуіңізді бақылап, қарқын мен кідірістерді сақтаңыз.',
            'Бақыт келерінде жігітті ұйықтатпайды, бақыт кетерінде жігітті оятпайды',
            NULL,
            2
        ),
        (
            'REPEAT_AFTER_AUDIO',
            'Табиғи қарқындағы сөйлеуді қайталау',
            'Диктордың табиғи, бірақ баяу сөйлеуін қайталаңыз.',
            'Жақсы болса алғаның, үйіңнен кісі кетпейді, жаман болса алғаның, тек жүргенге жетпейді',
            'exercise-references/stuttering/u5/ex3.wav',
            3
        )
        ) AS t(exercise_type, title, instruction, expected_text, reference_audio_url, order_index)
WHERE u.order_index = 5;

DROP TABLE IF EXISTS tmp_units;