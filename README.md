# Forks Desktop
Десктопное приложение для автоматизации вилок на JavaFX (Java 17)

## Telegram
Done! Congratulations on your new bot. You will find it at https://t.me/forks_desktop_chat_bot. You can now add a description, about section and profile picture for your bot, see /help for a list of commands. By the way, when you've finished creating your cool bot, ping our Bot Support if you want a better username for it. Just make sure the bot is fully operational before you do this.

Use this token to access the HTTP API:
6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg
Keep your token secure and store it safely, it can be used by anyone to control your bot.

### Request example
https://api.telegram.org/bot6061363285:AAGhtAmbN4A37_2IS7kx2zIvpZG8rRgcoGg/sendMessage?chat_id=-1001704593015&text=123

## Промежуточный сервер
Для оптимизации запросов к основному API был сделан REST API (FastAPI). См [main.py](/main.py)

## Защита от полупокеров
Каждый раз при запуске приложения с [сервера](http://nepolypoker.ru/flag.json) парсится поле flag. Если оно установленно в False, то приложение не запустится

## Гении разработчики

[Сергей](https://vk.com/melniknow) - погиб при весьма загадочных обстоятельствах

[Аркадий](https://vk.com/id236629299) - пропал без вести

## Лицензия
Приложение могут использовать 2 категории людей:
1) Не полупокеры
2) Те, кого разработчики отнесли к группе "First"