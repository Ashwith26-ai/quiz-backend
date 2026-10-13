/**
 * Online Quiz Management System - Quiz Engine & Countdown Timer
 */

document.addEventListener('DOMContentLoaded', function () {
    const timerDisplay = document.getElementById('timer-countdown');
    const timerBox = document.getElementById('quiz-timer-box');
    const quizForm = document.getElementById('quiz-form');
    const questionCards = document.querySelectorAll('.question-card');
    const prevBtn = document.getElementById('prev-question-btn');
    const nextBtn = document.getElementById('next-question-btn');
    const submitBtn = document.getElementById('submit-quiz-btn');
    const paletteButtons = document.querySelectorAll('.palette-btn');

    let currentQuestionIndex = 0;
    const totalQuestions = questionCards.length;

    // --- Timer Handling ---
    let remainingSeconds = parseInt(timerDisplay?.getAttribute('data-seconds') || '0', 10);

    function formatTime(seconds) {
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }

    function updateTimer() {
        if (!timerDisplay) return;

        timerDisplay.textContent = formatTime(remainingSeconds);

        if (remainingSeconds <= 30) {
            timerBox?.classList.remove('timer-warning');
            timerBox?.classList.add('timer-danger');
        } else if (remainingSeconds <= 120) {
            timerBox?.classList.add('timer-warning');
        }

        if (remainingSeconds <= 0) {
            clearInterval(timerInterval);
            timerDisplay.textContent = "00:00";
            alert("Time has expired! Your quiz will now be submitted automatically.");
            autoSubmitQuiz();
        } else {
            remainingSeconds--;
        }
    }

    let timerInterval = null;
    if (timerDisplay && remainingSeconds > 0) {
        updateTimer();
        timerInterval = setInterval(updateTimer, 1000);
    }

    function autoSubmitQuiz() {
        if (quizForm) {
            quizForm.submit();
        }
    }

    // --- Question Navigation ---
    function showQuestion(index) {
        if (index < 0 || index >= totalQuestions) return;
        currentQuestionIndex = index;

        questionCards.forEach((card, idx) => {
            if (idx === index) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });

        // Navigation buttons
        if (prevBtn) {
            prevBtn.disabled = (currentQuestionIndex === 0);
        }
        if (nextBtn) {
            nextBtn.style.display = (currentQuestionIndex === totalQuestions - 1) ? 'none' : 'inline-block';
        }
        if (submitBtn) {
            submitBtn.style.display = (currentQuestionIndex === totalQuestions - 1) ? 'inline-block' : 'none';
        }

        // Palette active highlight
        paletteButtons.forEach((btn, idx) => {
            if (idx === currentQuestionIndex) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    }

    // Next / Prev listeners
    prevBtn?.addEventListener('click', function () {
        if (currentQuestionIndex > 0) {
            showQuestion(currentQuestionIndex - 1);
        }
    });

    nextBtn?.addEventListener('click', function () {
        if (currentQuestionIndex < totalQuestions - 1) {
            showQuestion(currentQuestionIndex + 1);
        }
    });

    // Palette button listeners
    paletteButtons.forEach((btn) => {
        btn.addEventListener('click', function () {
            const qIndex = parseInt(this.getAttribute('data-index'), 10);
            showQuestion(qIndex);
        });
    });

    // Check answered questions and update palette
    function updateAnsweredStatus() {
        questionCards.forEach((card, idx) => {
            const selectedRadio = card.querySelector('input[type="radio"]:checked');
            const paletteBtn = document.getElementById(`palette-btn-${idx}`);
            if (selectedRadio) {
                paletteBtn?.classList.add('answered');
            } else {
                paletteBtn?.classList.remove('answered');
            }
        });
        updateAnsweredCount();
    }

    function updateAnsweredCount() {
        const answeredCountElem = document.getElementById('answered-count');
        if (answeredCountElem) {
            const answered = document.querySelectorAll('.palette-btn.answered').length;
            answeredCountElem.textContent = answered;
        }
    }

    // Listen for option selections
    document.querySelectorAll('.option-input').forEach((radio) => {
        radio.addEventListener('change', function () {
            updateAnsweredStatus();
        });
    });

    // Confirmation on manual submit
    quizForm?.addEventListener('submit', function (e) {
        const answered = document.querySelectorAll('.palette-btn.answered').length;
        const unanswered = totalQuestions - answered;

        let message = `Are you sure you want to submit your quiz?\n\nAnswered: ${answered}\nUnanswered: ${unanswered}`;
        if (!confirm(message)) {
            e.preventDefault();
        }
    });

    // Initialize first question
    if (totalQuestions > 0) {
        showQuestion(0);
        updateAnsweredStatus();
    }
});
