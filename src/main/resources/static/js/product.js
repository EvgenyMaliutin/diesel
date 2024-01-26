const counters = document.querySelectorAll('[data-counter]');

if(counters){
    counters.forEach(counter => {
        counter.addEventListener('click', e => {
            const target = e.target;

            if (target.closest('.counter__button')){
                let value = parseInt(target.closest('.counter').querySelector('input').value);
                let product_id = target.closest('.counter').querySelector('input').id;
                let balance = parseFloat(document.getElementById("balance_" + product_id).innerHTML);
                if (target.classList.contains('counter__button_plus')){
                    value++;
                } else{
                    -- value;
                }
                
                if(value < 1) {
                    value = 1;
                } else if (value > balance){
                    value = balance;
                }
                
                target.closest('.counter').querySelector('input').value = value;
                
            }
        })
    })
}

function get_id_name(id){
    return "balance_" + id;
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

