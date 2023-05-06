# Nowted

Это еще один мой PET-проект.

В данном проекте я реализовал функционал приложения для создания заметок с функцией изменения стиля текста и сортировки заметок по разлиным папкам. Дизайн приложения взят Figma Commnumity.

Проект отличается от моих предыдущих работ использованием нестандартного стека технологий. В отличие от моих прошлых работ, здесь я использовал DI-фреймворк Koin и библиотеку для навигации Voyager. Выбор Koin обусловлен желанием изучить особенности применения данного фреймворка, вследствие увеличения использования его в бизнес-проектах. Бибилотека Voyager представляет из себя библиотеку упрощенной навигации в приложениях, использующих Jetpack Compose, и пожет быть использована как в Android приложениях, так и в Compose Muliplatform, но в этом случае я использовал Android модули.

Помимо новых технологий, целью при написании данного приложения было отточить навык разработки Compose приложений с различными параметрами содержимого экрана в зависимости от размера девайса пользователя. По изначальному дизайну, приложение было адаптировано только для широких экранов, но в проекте реализована также система поэкранной навигации для небольших экранов.

Итак, в проекте используются следующие технологии и фреймворки:

1. **Jetpack Compose** для построения UI;
2. Внедрение зависимостей (DI) при помощи **Koin**;
3. Навигация в приложении при помощи библиотеки **Voyager**;
4. Для хранения данных используется **Room Database**;
7. Управление зависимостями осуществляется при помощи единого **.toml** файла, вместо модуля с зависимостями, как в прошлых проектах.

## Скриншоты приложения

### Мобильный режим

<p float="left">
  <img src="https://github.com/FabledTria5/Nowted/blob/master/images/mobile_home_screen.png" alt="Главный экран" width = "310" height = "700">
  <img src="https://github.com/FabledTria5/Nowted/blob/master/images/mobile_folder_screen.png" alt="Экран папки" width = "310" height = "700">
  <img src="https://github.com/FabledTria5/Nowted/blob/master/images/mobile_note_screen.png" alt="Экрна редактирования" width = "310" height = "700">
</p>

<p float="left">
  <img src="https://github.com/FabledTria5/Nowted/blob/master/images/mobile_creating_folder.png" alt="Создание папки" width = "310" height = "700">
  <img src="https://github.com/FabledTria5/Nowted/blob/master/images/mobile_folder_screen_empty.png" alt="Пустой экран папки" width = "310" height = "700">
  <img src="https://github.com/FabledTria5/Nowted/blob/master/images/mobile_text_styling.png" alt="Стилизация текста" width = "310" height = "700">
</p>

### Планшетный режим

<img src="https://github.com/FabledTria5/Nowted/blob/master/images/expanded_idle.png" alt="При открытии приложения">
<img src="https://github.com/FabledTria5/Nowted/blob/master/images/expanded_creating_folder.png" alt="Создание папки">
<img src="https://github.com/FabledTria5/Nowted/blob/master/images/expanded_note_selected.png" alt="Выбран документ">
<img src="https://github.com/FabledTria5/Nowted/blob/master/images/expanded_text_styling.png" alt="Стилизация текста">
