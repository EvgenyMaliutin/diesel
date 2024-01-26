const counters = document.querySelectorAll('[data-counter]');

if(counters){
    counters.forEach(counter => {
        counter.addEventListener('click', e => {
            const target = e.target;

            if (target.closest('.counter__button')){
                let value = parseInt(target.closest('.counter').querySelector('input').value);
                if (target.classList.contains('counter__button_plus')){
                    value++;
                } else{
                    -- value;
                }

                if(value < 1) {
                    value = 1;
                }

                target.closest('.counter').querySelector('input').value = value;

            }
        })
    })
}

function validateInput(inputElement){
    var inputValue = inputElement.value;

    // Проверяем, что введены только цифры
    if (/[^0-9]/.test(inputValue)) {
        // Если введены не цифры, очищаем поле от некорректных символов
        inputElement.value = inputValue.replace(/[^0-9]/g, '');
    }

    // Проверяем, что заказано 1 или более товаров
    var amount = parseInt(inputElement.value);
    if (isNaN(amount) || amount <= 0) {
        // Если введено 0 или отрицательное число, устанавливаем значение в 1
        inputElement.value = '1';
    }
}

function showConfirmationDialog(event) {
    event.preventDefault();
    Swal.fire({
        title: 'Требуется авторизация',
        text: 'После авторизации товары из анонимной корзины будут добавлены в Вашу основную корзину. Продолжить?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Да, продолжить',
        cancelButtonText: 'Отмена',
    }).then((result) => {
        if (result.isConfirmed) {
            // Код, который выполнится при нажатии "Да"
            console.log('Пользователь согласился');
            document.getElementById('confirm_order').submit();
        }
    });
}