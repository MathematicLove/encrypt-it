/**
 * Основной JavaScript файл для EncryptIt
 */

document.addEventListener('DOMContentLoaded', function() {
    // Инициализация табов
    initTabs();

    // Валидация форм
    initFormValidation();

    // Показ уведомлений
    initNotifications();

    // Инициализация счетчика символов
    initCharCounter();

    // Инициализация загрузки файлов
    initFileUpload();

    // Инициализация проверки силы пароля
    initPasswordStrength();

    // Добавление анимаций при загрузке
    animateOnLoad();
});

/**
 * Инициализация системы табов
 */
function initTabs() {
    const tabs = document.querySelectorAll('.tab[data-tab]');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');

            // Удаляем активный класс у всех табов
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(content => {
                content.classList.remove('active');
            });

            // Добавляем активный класс текущему табу
            this.classList.add('active');
            const targetContent = document.getElementById(tabId);
            if (targetContent) {
                targetContent.classList.add('active');
            }
        });
    });
}

/**
 * Инициализация валидации форм
 */
function initFormValidation() {
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        // Валидация при вводе
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });

            input.addEventListener('input', function() {
                if (this.classList.contains('error')) {
                    validateField(this);
                }
            });
        });

        // Валидация при отправке
        form.addEventListener('submit', function(event) {
            let isValid = true;
            const requiredFields = this.querySelectorAll('[required]');

            requiredFields.forEach(field => {
                if (!validateField(field)) {
                    isValid = false;
                }
            });

            // Валидация ключа шифрования
            const keyFields = this.querySelectorAll('input[name="key"]');
            keyFields.forEach(field => {
                if (field.value.trim().length < 3) {
                    isValid = false;
                    highlightError(field, 'Ключ должен содержать минимум 3 символа');
                }
            });

            // Валидация файлов
            const fileFields = this.querySelectorAll('input[type="file"]');
            fileFields.forEach(field => {
                if (field.files.length > 0) {
                    const file = field.files[0];
                    const maxSize = 10 * 1024 * 1024; // 10MB

                    if (file.size > maxSize) {
                        isValid = false;
                        highlightError(field, 'Файл слишком большой. Максимальный размер: 10MB');
                    }
                }
            });

            if (!isValid) {
                event.preventDefault();
                showNotification('Пожалуйста, заполните все обязательные поля правильно', 'error');
                // Прокрутка к первой ошибке
                const firstError = form.querySelector('.error');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstError.focus();
                }
            } else {
                // Показываем индикатор загрузки
                const submitBtn = form.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<span class="loading"></span> Обработка...';
                }
            }
        });
    });
}

/**
 * Валидация отдельного поля
 */
function validateField(field) {
    const value = field.value.trim();
    let isValid = true;

    if (field.hasAttribute('required') && !value) {
        isValid = false;
        highlightError(field, 'Это поле обязательно для заполнения');
    } else if (field.type === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            isValid = false;
            highlightError(field, 'Введите корректный email адрес');
        } else {
            removeError(field);
        }
    } else {
        removeError(field);
    }

    return isValid;
}

/**
 * Подсветка поля с ошибкой
 */
function highlightError(field, message = 'Это поле обязательно для заполнения') {
    field.classList.add('error');

    // Создаем сообщение об ошибке
    let errorMessage = field.parentNode.querySelector('.field-error');
    if (!errorMessage) {
        errorMessage = document.createElement('div');
        errorMessage.className = 'field-error';
        field.parentNode.appendChild(errorMessage);
    }

    errorMessage.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    
    // Анимация появления ошибки
    errorMessage.style.animation = 'slideInMessage 0.3s ease-out';
}

/**
 * Удаление подсветки ошибки
 */
function removeError(field) {
    field.classList.remove('error');

    const errorMessage = field.parentNode.querySelector('.field-error');
    if (errorMessage) {
        errorMessage.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => errorMessage.remove(), 300);
    }
}

/**
 * Инициализация системы уведомлений
 */
function initNotifications() {
    // Проверяем наличие параметров в URL для показа уведомлений
    const urlParams = new URLSearchParams(window.location.search);

    if (urlParams.has('success')) {
        showNotification('Операция выполнена успешно!', 'success');
    }

    if (urlParams.has('error')) {
        showNotification('Произошла ошибка. Пожалуйста, попробуйте снова.', 'error');
    }
}

/**
 * Показ уведомления
 */
function showNotification(message, type = 'info') {
    // Удаляем существующие уведомления
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(n => n.remove());

    // Создаем элемент уведомления
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    
    const icon = type === 'success' ? 'fa-check-circle' : 
                 type === 'error' ? 'fa-exclamation-circle' : 
                 'fa-info-circle';
    
    notification.innerHTML = `
        <i class="fas ${icon}"></i>
        <span>${message}</span>
    `;

    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 16px 24px;
        border-radius: 12px;
        color: white;
        font-weight: 500;
        z-index: 10000;
        display: flex;
        align-items: center;
        gap: 12px;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
        animation: slideInNotification 0.3s ease-out;
        max-width: 400px;
    `;

    // Устанавливаем цвет в зависимости от типа
    if (type === 'success') {
        notification.style.background = 'linear-gradient(135deg, #38a169 0%, #2f855a 100%)';
    } else if (type === 'error') {
        notification.style.background = 'linear-gradient(135deg, #e53e3e 0%, #c53030 100%)';
    } else {
        notification.style.background = 'linear-gradient(135deg, #3182ce 0%, #2c5282 100%)';
    }

    // Добавляем в DOM
    document.body.appendChild(notification);

    // Удаляем через 5 секунд
    setTimeout(() => {
        notification.style.animation = 'slideOutNotification 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    }, 5000);

    // Добавляем стили для анимации
    if (!document.querySelector('#notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            @keyframes slideInNotification {
                from {
                    transform: translateX(calc(100% + 20px));
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            
            @keyframes slideOutNotification {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(calc(100% + 20px));
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }

    // Возможность закрыть уведомление кликом
    notification.addEventListener('click', () => {
        notification.style.animation = 'slideOutNotification 0.3s ease-out';
        setTimeout(() => notification.remove(), 300);
    });
}

/**
 * Показ/скрытие пароля
 */
function togglePasswordVisibility(inputId) {
    const input = document.getElementById(inputId);
    const eyeIcon = document.getElementById(inputId + '-eye');
    
    if (!input || !eyeIcon) return;

    if (input.type === 'password') {
        input.type = 'text';
        eyeIcon.classList.remove('fa-eye');
        eyeIcon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        eyeIcon.classList.remove('fa-eye-slash');
        eyeIcon.classList.add('fa-eye');
    }
}

/**
 * Генерация случайного ключа
 */
function generateKey(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return;

    const length = 16;
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*';
    let key = '';

    for (let i = 0; i < length; i++) {
        key += chars.charAt(Math.floor(Math.random() * chars.length));
    }

    input.value = key;
    input.focus();
    
    // Анимация при генерации
    input.style.transform = 'scale(1.05)';
    setTimeout(() => {
        input.style.transform = 'scale(1)';
    }, 200);

    showNotification('Ключ успешно сгенерирован', 'success');
    
    // Копируем в буфер обмена
    copyToClipboard(key);
}

/**
 * Копирование текста в буфер обмена
 */
function copyToClipboard(text) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(() => {
            // Уведомление уже показано в generateKey
        }).catch(err => {
            console.error('Ошибка при копировании: ', err);
        });
    } else {
        // Fallback для старых браузеров
        const textArea = document.createElement('textarea');
        textArea.value = text;
        textArea.style.position = 'fixed';
        textArea.style.opacity = '0';
        document.body.appendChild(textArea);
        textArea.select();
        try {
            document.execCommand('copy');
        } catch (err) {
            console.error('Ошибка при копировании: ', err);
        }
        document.body.removeChild(textArea);
    }
}

/**
 * Инициализация счетчика символов
 */
function initCharCounter() {
    const textarea = document.getElementById('text');
    const charCounter = document.getElementById('textCharCount');
    
    if (textarea && charCounter) {
        textarea.addEventListener('input', function() {
            charCounter.textContent = this.value.length;
            
            // Изменяем цвет при достижении лимита
            if (this.value.length > 10000) {
                charCounter.style.color = '#e53e3e';
            } else if (this.value.length > 5000) {
                charCounter.style.color = '#d69e2e';
            } else {
                charCounter.style.color = '#718096';
            }
        });
    }
}

/**
 * Инициализация загрузки файлов
 */
function initFileUpload() {
    const fileInputs = document.querySelectorAll('input[type="file"]');
    
    fileInputs.forEach(input => {
        const fileNameSpan = input.parentNode.querySelector('.file-name');
        
        input.addEventListener('change', function() {
            if (this.files && this.files.length > 0) {
                const file = this.files[0];
                const fileName = file.name;
                const fileSize = (file.size / 1024 / 1024).toFixed(2);
                
                if (fileNameSpan) {
                    fileNameSpan.textContent = `${fileName} (${fileSize} MB)`;
                    fileNameSpan.style.display = 'block';
                }
                
                // Анимация при выборе файла
                const label = input.parentNode.querySelector('.file-upload-label');
                if (label) {
                    label.style.borderColor = '#38a169';
                    label.style.background = 'rgba(56, 161, 105, 0.1)';
                    setTimeout(() => {
                        label.style.borderColor = '';
                        label.style.background = '';
                    }, 1000);
                }
            } else if (fileNameSpan) {
                fileNameSpan.textContent = '';
                fileNameSpan.style.display = 'none';
            }
        });

        // Drag and drop
        const label = input.parentNode.querySelector('.file-upload-label');
        if (label) {
            label.addEventListener('dragover', function(e) {
                e.preventDefault();
                this.style.borderColor = '#667eea';
                this.style.background = 'rgba(102, 126, 234, 0.1)';
            });

            label.addEventListener('dragleave', function(e) {
                e.preventDefault();
                this.style.borderColor = '';
                this.style.background = '';
            });

            label.addEventListener('drop', function(e) {
                e.preventDefault();
                this.style.borderColor = '';
                this.style.background = '';
                
                if (e.dataTransfer.files.length > 0) {
                    input.files = e.dataTransfer.files;
                    input.dispatchEvent(new Event('change', { bubbles: true }));
                }
            });
        }
    });
}

/**
 * Инициализация проверки силы пароля
 */
function initPasswordStrength() {
    const passwordInput = document.getElementById('password');
    const strengthIndicator = document.getElementById('passwordStrength');
    
    if (passwordInput && strengthIndicator) {
        passwordInput.addEventListener('input', function() {
            const password = this.value;
            let strength = 0;
            
            if (password.length >= 8) strength++;
            if (password.length >= 12) strength++;
            if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
            if (/\d/.test(password)) strength++;
            if (/[^a-zA-Z\d]/.test(password)) strength++;
            
            strengthIndicator.className = 'password-strength';
            
            if (password.length === 0) {
                strengthIndicator.className = 'password-strength';
            } else if (strength <= 2) {
                strengthIndicator.className = 'password-strength weak';
            } else if (strength <= 3) {
                strengthIndicator.className = 'password-strength medium';
            } else {
                strengthIndicator.className = 'password-strength strong';
            }
        });
    }
}

/**
 * Анимации при загрузке страницы
 */
function animateOnLoad() {
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(() => {
            card.style.transition = 'all 0.5s ease-out';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });

    const formGroups = document.querySelectorAll('.form-group');
    formGroups.forEach((group, index) => {
        group.style.opacity = '0';
        setTimeout(() => {
            group.style.transition = 'opacity 0.3s ease-out';
            group.style.opacity = '1';
        }, index * 50);
    });
}
