# Документация JM Developer Social
## Работа с git
### Клонирование проекта

1. На странице репозитория убедитесь, что выбрана ветка **dev** (1), нажмите кнопку **Clone** (2), скопируйте ссылку (3).

![](src/main/resources/static/images/git_tutor/git_clone_url.png)

2. Откройте **Intellij IDEA**, нажмите **Get from version control** на экране приветствия, либо **VCS | Git | Clone...** в меню.

![](src/main/resources/static/images/git_tutor/git_clone_get.png)

![](src/main/resources/static/images/git_tutor/git_clone_get_alt.png)

3. Вставьте скопированную ссылку в строку **URL**, нажмите **Clone**.

![](src/main/resources/static/images/git_tutor/git_clone_clone.png)

### Перед внесением изменений в код
Создайте новую ветку в git-репозитории и работайте в ней. Для этого:
1. Нажмите на текущую ветку **dev** в правом нижнем углу.


![](src/main/resources/static/images/git_tutor/git_branch.png)

2. Выберите **New branch**.

![](src/main/resources/static/images/git_tutor/git_branch_create.png)

3. Введите название своей новой ветки (на ваше усмотрение) и нажмите **Create**.

![](src/main/resources/static/images/git_tutor/git_branch_name.png)

### Добавление своего кода в общий репозиторий. Git push.

Прежде чем создать merge request вам необходимо подготовить вашу ветку к отправке в общий репозиторий.

1. Нажмите на текущую ветку в правом нижнем углу. Выберите опцию **dev | update**. 
Таким образом вы скачаете в свою локальную ветку **dev** все коммиты которые были замержены, 
пока вы работали в своей ветке.

![](src/main/resources/static/images/git_tutor/git_premerge_update_dev.png)

2. Убедитесь, что в данный момент активна ваша рабочая ветка (занчек ярлыка слева от имени, как у ветки my-branch на скриншоте).
Выберите опцию **dev | Merge into Current**. Таким образом вы добавите все изменения из ветки **dev** в вашу ветку. При возникновении конфликтов разрешите их.

![](src/main/resources/static/images/git_tutor/git_premerge_merge_dev.png)

3. ---**ВАЖНО**--- Убедитесь что проект собирается и запускается.

4. Выберите вашу ветку и нажмите на **Push...** чтобы добавить её в общий репозиторий.

![](src/main/resources/static/images/git_tutor/git_premerge_push.png)

### Создание merge request

1. Создайте новый merge request. В качестве **Source branch** выберите свою ветку, **Target branch** - **dev**.

![](src/main/resources/static/images/git_tutor/git_merge_req.png)

![](src/main/resources/static/images/git_tutor/git_merge_req_new.png)

![](src/main/resources/static/images/git_tutor/git_merge_req_src_trg.png)

2. Проверьте данные, допишите комментарии при необходимости. Обратите внимание на опцию **Delete source branch when merge request is accepted**.
Завершите создание реквеста, приложите ссылку на него в карточку таска на Trello.

![](src/main/resources/static/images/git_tutor/git_merge_req_final.png)

## Как настроить запуск
- Скачайте и установите PostgreSQL 11 или 12 версии.
- Создайте базу данных и схему в ней, настройте доступ к БД в IntellijIdea (название БД, схемы, пользователя и пароль
 см. application-local.properties).
- В проекте реализованно два профиля: **local** и **dev**. Для выбора профиля, добавьте в program arguments
 (Run->Edit Configurations)**--spring.profiles.active=local** или **--spring.profiles.active=dev** соответственно.
 
  ![](src/main/resources/static/images/program_param.jpg)
  
  
- если в директории test папка resources не установлена в качестве источника ресурсов, зайти в Project Structure Modules
и поставить отметку на папке.

  ![](src/main/resources/static/images/test_resources_mark.jpg) 
  
  
- для корректной работы MapStruct назначить Maven выполнение операции clean перед каждым запуском приложения. Для чего
перейти к настройкам конфигурации запуска приложения (Run->Edit Configurations) и в поле Before launch
(выполнить прокрутку до самого низа окна) добавить задачу Maven clean. Простым перетаскиванием установить ее перед 
Build.


  ![](src/main/resources/static/images/clean_goal.jpg)

- установите плагин Lombok. Перейдите File → Settings → Plugins. В открывшемся окне плагинов введите «Lombok» в строке
 поиска а нажмите кнопку «Install».

## DevOps
Адрес удаленного сервера: 91.241.64.181:5561, для доступа к серверу по ssh, достаточно ввести в терминал: 
ssh user@91.241.64.181, пароль необходимо уточнить у ментора.

Для деплоя используется docker и jenkins, в dockere созданы образы postgres 13 и приложения, собранным из dockerfile, 
с именем platform, на основе данных образов создаются контейнеры db и app, 
которые запускаются с помощью утилиты docker-compose, настройка билда расписана в соответствующем файле.

Для подтверждения работы контейнеров необходимо в терминал прописать docker ps (появится список работающих контейнеров), предварительно подключившись по ssh

Для доступа к jenkins достаточно перейти по адресу: http://91.241.64.181:8080, логин и пароль уточнять у ментора.
В jenkins создан пайплайн с именем platform, который обновляет билд проекта в докере и перезапускает контейнер app, 
тригером для выполнения platfom является push или merge request на dev ветку, для этого в гитлабе был создан хук в разделе
settings -> webhooks, также тут есть возможность выполнить тестовый тригер для jenkins. Для доступа jenkins к репозиторию гита, ипользуется корпоративная почта, за подробностями обращаться к ментору.
Инструкция по обновлению докер контейнера дженкинсом прописаны в jenkinsfile.

## Сущности

### Users

#### Поля:

- **userId** - уникальный идентификационный номер пользователя;
- **first_name** - имя пользователя;
- **last_name** - фамилия пользователя;
- **date_of_birth** - дата рождения пользователя;
- **education** - образование пользователя;
- **about_me** - информация о пользователе;
- **image** - аватарка пользователя;
- **email** - адрес электронной почты пользователя;
- **profession** - профессия пользователя;
- **status** - идентификатор статуса пользователя;
- **active_id** - идентификатор состояния пользователя;
- **password** - пароль пользователя;
- **persist_date** - время регистрации пользователя;
- **last_redaction_date** - дата последней редакции;
- **is_enable** - подтверждение почты пользователя;
- **role_id** - идентификационный номер пользователя;
- **city** - город рождения/проживания пользователя;
- **link_site** - ссылка на сайт пользователя;



```
Таблица, которая описывает основной пользовательский функционал, который соответствует стандартному
функционалу социальных сетей: переписка с другими пользователями, посты, добавление различных медиа документов. 
Наделен ролью. Связан с остальными сущностями через уникальный идентификационный номер.
```

### Role

#### Поля:

- **id** - уникальный идентификационный номер роли;
- **name** - наименование роли;
```
Определяет порядок прав действий пользователя в системе.
```

### Language

#### Поля:

- **id** - уникальный идентификационный номер языка;
- **name** - наименование языка;
```
Определяет набор языков пользователей в системе.
```

### UserLanguages

#### Поля:

- **user_id** - уникальный идентификационный номер пользователя;
- **language_id** - уникальный идентификационный номер языка;
```
Производная таблица связи many-to-many сущностей пользователей и языков.
```

### UserHasAlbum

#### Поля:

- **id** - уникальный идентификационный номер записи;
- **persist_date** - дата создания записи;
- **album_id** - уникальный идентификационный номер альбома;
- **user_id** - уникальный идентификационный номер пользователя, сохранившего альбом;
```
Таблица, сдержащая информацию о альбом, сохранённых пользователями.
```

### UserTabs

#### Поля:

- **id** - уникальный идентификационный номер записи на стене;
- **persist_date** -  дата создания записи;
- **post-id** - уникальный идентификационный номер поста;
- **user_id** - уникальный идентификационный номер пользователя, сделавшего запись;
```
Таблица, содержащая информаию о записях на стене пользователя;
```

### UsersAudiosCollections

#### Поля:

- **user_id** - уникальный идентификационный номер пользователя;
- **audio_id** - уникальный идентификационный номер аудиозаписи;
```
Таблица, содержащая информацию об аудио коллекциях пользователей.
```

### UsersVideosCollections

#### Поля:

- **user_id** - уникальный идентификационный номер пользователя;
- **video_id** - уникальный идентификационный номер видеозаписи;
```
Таблица, содержащая информацию о видео коллекциях пользователей.
```

### UsersGroupChats

#### Поля:

- **user_id** - уникальный идентификационный номер пользователя;
- **group_chat_id** - уникальный идентификационный группового чата;
```
Таблица, содержащая информацию о групповых чата пользователей.
```

### Friends

#### Поля:

- **id** - уникальный идентификационный номера связи сущностей пользователя и друга;
- **user_id** - уникальный идентификационный номер пользователя;
- **user_id** - уникальный идентификационный номер друга пользователя;
```
Таблица, которая связывает поле пользователя с полем его друга через связь many-to-one.
```

### Followers

#### Поля:

- **id** - уникальный идентификационный номера связи сущностей пользователя и его подписчиков;
- **user_id** - уникальный идентификационный номер пользователя;
- **follower_id** - уникальный идентификационный номер подписчика пользователя;
```
Таблица, которая связывает поле пользователя с полями его друзей через связь many-to-one.
```

### Active

#### Поля:

- **id** - уникальный идентификационный номер активности;
- **name** - наименование активности;
```
Определяет набор статусов активности пользователей в системе.
```

### Groups

#### Поля:

- **id** - уникальный идентификационный номер группы;
- **name** - наименование группы;
- **persist_date** - дата создания группы;
- **last_redaction_date** - дата последней редакции группы;
- **link_site** - ссылка на сайт;
- **category_id** - уникальный идентификационный номер категории группы;
- **owner_id** - уникальный идентификационный номер владельца группы;
- **description** - описание группы;
- **address_image_group** - ссылка на логотип группы;
```
Таблица, которая содержит в себе записи обо всех существующих группах с информацией о ней и ее владельце.
У группы есть связанная сущность "Стена", на которой пользователи могут выкладывать посты, комментировать.
их, лайкать.  
```

### GroupCategory

#### Поля:

- **id** - уникальный идентификационный номер категории;
- **category** - название категории;
```
Таблица, содержащая информацию о категории группы;
```
### GroupHasUser

#### Поля

- **id** - уникальный идентификационный номера связи сущностей группы и ее подписчиков;
- **group_id** - уникальный идентификационный номер группы;
- **user_id** - уникальный идентификационный номер пользователя;
- **persist_date** - дата подписки пользователя;
```
Таблица, которая связывает информацию об уникальных идентификационных номерах
группы и пользователя и дату образования этой связи.
```

### GroupCategory

#### Поля:

- **id** - уникальный идентификационный номер категории;
- **category** - наименование категории;
```
Сущность, которая связывает уникальный идентификационный номер категории и ее наименование.
```

### GroupWall

#### Поля:

- **group_id** - уникальный идентификационный номер группы;
- **post_id** - уникальный идентификационный номер поста;
```
Производная таблица, которая связывает поле номера группы с полями номеров постов через связь one-to-many.
```

### Bookmarks

#### Поля:

- **id** - уникальный идентификационный номер закладки;
- **user_id** - уникальный идентификационный номер сохранившего пользователя;
- **post_id** - уникальный идентификационный номер поста;
- **persist_date** - дата создания закладки;
```
Производная таблица, которая связывает поле номера сохранившего пользователя с полями номеров 
постов через связь one-to-many.
```

### Posts

#### Поля:

- **id** - уникальный идентификационный номер поста;
- **title** - наименование поста;
- **text** - текстовая информация в посте;
- **user_id** - уникальный идентификационный номер пользователя-владельца поста;
- **persist_date** - дата создания поста;
- **last_redaction_date** - дата последней редакции поста;
```
Таблица, которая инициализирует информацию о посте.
```

### PostMedia

#### Поля:

- **post_id** - уникальный идентификационный номер поста;
- **media_id** - уникальный идентификационный номер медиа документа;
```
Производная таблица, которая связывает поле номера поста с полями номеров медиа документов через связь one-to-many.
```

### PostTags

#### Поля:

- **post_id** - уникальный идентификационный номер поста;
- **tags_id** - уникальный идентификационный номер тэга;
```
Производная таблица связей Many-to-many тэгов и постов;
```

### Tags

#### Поля:

- **id** - уникальный идентификационный номер тэга;
- **text** - текст тэга;
```
Таблица, содержащая информацию о тэгах.
```

### Comments

#### Поля:

- **id** - уникальный идентификационный номер комментария;
- **user_id** - уникальный идентификационный номер пользователя;
- **comment_type** - тип комментария;
- **comment** - текстовая информация в комментарии;
- **persist_date** - время создания комментария;
- **last_redaction_date** - время последней редакции комментария;
```
Таблица, которая содержит информацию о созданном комментарии.
```

### CommentLike

#### Поля:

- **like_id** - уникальный идентификационный номер поставленного лайка;
- **comment_id** - уникальный идентификационный номер комментария;
```
Таблица, которая связывает информацию о лайке и номере комментария.
```

### PostComments

#### Поля:

- **post_id** - уникальный идентификационный номер поста;
- **comment_id** - уникальный идентификационный номер комментария;
```
Таблица, которая связывает информацию о номере поста и комментария.
```

### PostLike

#### Поля:

- **like_id** - уникальный идентификационный номер поставленного лайка;
- **post_id** - уникальный идентификационный номер поста;
```
Таблица, которая связывает информацию о номере лайка и поста.
```

### Like

#### Поля:

- **id** - уникальный идентификационный номер лайка;
- **user_id** - уникальный идентификационный номер пользователя;
- **like_type** - тип лайка;
```
Таблица, которая содержит информацию о созданном лайке.
```

### MessageLike

#### Поля:

- **like_id** - уникальный идентификационный номер лайка;
- **message_id** - уникальный идентификационный номер сообщения;
```
Производная таблица связей One-to-one сообщений и лайков;
```

### MediaLikes

#### Поля:

- **like_id** - уникальный идентификационный номер лайка;
- **media_id** - уникальный идентификационный номер медиа документа;
```
Производная таблица, которая связывает поле номера лайка с полями номеров медиа документов через связь one-to-many.
```

### MediaComment

#### Поля:

- **comment_id** - уникальный идентификационный номер комментария;
- **media_id** - уникальный идентификационный номер медиа документа;
```
Производная таблица, которая связывает поле номера комментария с полями номеров медиа 
документов через связь one-to-many.
```

### Messages

#### Поля:

- **id** - уникальный идентификационный номер сообщения;
- **message** - вложенная в сообщение информация;
- **is_unread** - статус прочтения посланного сообщения;
- **persist_date** - время создания сообщения;
- **last_redaction_date** - время последней редакции сообщения;
- **user_id** - идентификационный номер юзера, отправившего сообщение;
```
Таблица, которая содержит информацию о созданном сообщении. 
```

### SingleChat

#### Поля:

- **id** - уникальный идентификационный номер чата;
- **image** - лого чата;
- **persist_date** - время создания чата;
- **user_one_id** - уникальный идентификационный номер пользователя-отправителя;
- **user_two_id** - уникальный идентификационный номер пользователя-получателя;
```
Таблица, которая содержит информацию о чате, в том числе one-to-one связь с уникальным 
идентификационным номером пользователя-отправителя и пользователя-получателя, а также
связь one-to-many c уникальным идентификационным номером пользователя. В чате происходит
обмен данными различного типа между пользователями.
```

### SingleChatMessages

#### Поля:

- **chat_id** - уникальный идентификационный номер чата;
- **message_id** - уникальный идентификационный номер сообщения;
```
Производная таблица one-to-many связей чата и сообщений.
```

### GroupChats

#### Поля:

- **id** - уникальный идентификационный номер группового чата;
- **image** - лого чата;
- **persists_date** - дата создания группового чата;
- **title** - название группового чата;
```
Таблица содержит основную информацию о групповом чате.
```

### GroupChatMessages

#### Поля:

- **chat_id** - уникальный идентификационный номер группового чата;
- **message_id** - уникальный идентификационный номер сообщения;
```
Производная таблица one-to-many связей групповго чата и сообщений.
```


### GroupChatsUsers

#### Поля:

- **group_chat_id** - уникальный идентификационный номер группового  чата4 
- **users_user_id** - уникальный идентификационный номер пользователя;
```
Производная таблица связей many-to-many групповых чатов и пользователей.
```

### Media

#### Поля:

- **id** - уникальный идентификационный медиа документа;
- **user_id** - уникальный идентификационный номер пользователя, создавшего медиа документа;
- **url** - ссылка на медиа документ;
- **media_type** - тип медиа документа;
- **persist_date** - время создания медиа документа;
- **album_id** - уникальный идентификационный номер альбома;
```
Таблица, которая содержит информацию о медиа документе для возможности предоставления его самого
и информации о нем пользователю. Медиа документ может быть представлен в виде видое, изображения или
аудиозаписи.
```

### MediaMessages

#### Поля:

- **message_id** - уникальный идентификационный номер сообщения;
- **media_id** - уникальный идентификационный номер медиа документа;
```
Производная таблица, которая связывает поле номера сообщения с полями номеров 
медиа документов через связь one-to-many.
```

### Albums

#### Поля:

- **id** - уникальный идентификационный номер альбома;
- **name** - наименование альбома;
- **icon** - лого альбома;
- **persist_date** - время создания альбома;
- **media_type** - тип содержащихся медиа документов;
- **user_owner-id** - уникальный идентификационный номер пользователя-владельца альбома;
```
Таблица, которая содержит информацию об альбоме.
```

### AlbumAudios

#### Поля:

- **album_id** - уникальный идентификационный номер аудио альбома;
```
Таблица, содаржащая идентификационные номера аудио альбомов.
```

### AlbumHasAudio

#### Поля:

- **album_id** - уникальный идентификационный номер аудио альбома;
- **audios_id** - уникальный идентификационный номер аудио записи;
```
Производная таблица связей Many-to-many аудио албомов и аудио записей.
```

### AlbumHasImage

#### Поля:

- **album_id** - уникальный идентификационный номер фото альбома;
- **image_id** - уникальный идентификационный номер изображения;
```
Производная таблица связей Many-to-many фото албомов и изображений.
```

### AlbumHasVideo

#### Поля:

- **album_id** - уникальный идентификационный номер видео альбома;
- **video_id** - уникальный идентификационный номер видео записи;
```
Производная таблица связей Many-to-many фидео албомов и видео записей.
```

### AlbumImage

#### Поля:

- **album_id** - уникальный идентификационный номер фото альбома;
```
Таблица, содаржащая идентификационные номера фото альбомов.
```

### AlbumVideo

#### Поля:

- **album_id** - уникальный идентификационный номер видео альбома;
```
Таблица, содаржащая идентификационные номера видео альбомов.
```

### Playlists

#### Поля:

- **playlist_id** - уникальный идентификационный номер плейлиста;
- **image** - логотип плейлиста;
- **name** - названий плейлиста;
- **persist_date** - дата создания плейлиста;
- **user_id** - уникальный идентификационный номер пользователя-создателя плейлиста;
```
Таблица, содержащая информацию о плейлистах.
```

### PlaylistHasAudio

#### Поля:

- **playlist_id** - уникальный идентификационный номер плейлиста;
- **audios_id** - уникальный идентификационный номер аудио файла;
```
Производная таблица связей Many-to-many плейлиста и аудио записей.
```

### Audios

#### Поля:

- **media_id** - уникальный идентификационный номер медиа документа;
- **icon** - иконка для аудио файла;
- **name** - имя для аудио файла;
- **author** - автор для аудио файла;
- **album** - альбом для аудио файла;
- **length** - длина аудио файла;
```
Таблица, которая связывает информацию о медиа документе типа AUDIO
```

### Videos

#### Поля:

- **media_id** - уникальный идентификационный номер медиа документа;
- **icon** - иконка для видео файла;
- **name** - наименование аудио документа;
- **author** - наименование автора аудио файла;
```
Таблица, которая связывает информацию о номере медиа документа и его иконки
```

### Images

#### Поля:

- **media_id** - уникальный идентификационный номер медиа документа;
- **description** - иконка для аудио файла;
```
Таблица, которая связывает информацию о номере медиа документа и его описании 
```

### Reposts

#### Поля:

- **id** - уникальный идентификационный номер репоста;
- **post_id** - уникальный идентификационный номер поста;
- **user_id** - уникальный идентификационный номер пользователя;
```
Производная таблица, которая связывает поле номера поста с полями номеров пользователей через связь many-to-many.
```

[Схема](https://dbdiagram.io/d/5f5f70927da1ea736e2dc8c9)


##Как пользоваться конвертором MapStruct.

**MapStruct** - это генератор кода, который значительно упрощает реализацию сопоставлений между типами Java-компонентов
 на основе подхода соглашения по конфигурации.
Сгенерированный код сопоставления использует простые вызовы методов 
и, следовательно, является быстрым, безопасным по типам и простым для понимания.
Более подробно можно ознакомиться в официальной документации:https://mapstruct.org/ .

В текущем проекте **Developer Social** технология **MapStruct** используется,в основном, для 
преобразования **Dto** в **Entity** и наоборот.
Названия всех классов преобразования должны заканчиваться на «**Converter**» (например: **GroupChatConverter**) и должны храниться в пакете **converters**.
Такой класс должен быть абстрактным, помеченным аннотацией **@Mapper**.Эта аннотация отмечает класс
как класс сопоставления и позволяет процессору **MapStruct** включиться во время компиляции.
Методы должны быть абстрактными,из их названия должно быть явно понятно,какой класс
во что преобразуется. Например: (**chatDtoToGroupChat**- преобразует **ChatDto** в **GroupChat**).

Если соответствующие поля двух классов имеют разные названия, для их сопоставления
используется аннотация **@Mapping**. Эта аннотация ставится над абстрактным методом преобразования
и имеет следующие обязательные поля:

**source** - исходное поле преобразовываемого класса.
**target**- конечное поле класса,в котором должно быть значение из **source**.

Для разрешения неоднозначностей в именах полей классов можно указывать их с именем
соответствующего параметра метода преобразования.
например:(**source** = "**chatDto.title**", где **chatDto** - имя параметра метода преобразования)

По умолчанию, метод должен принимать объект преобразовываемого класса, но также
можно передовать различные другие параметры(например **Id**) и потставлять их в **source**,
чтобы в дальнейшем поле **target** приняло это значение.

Могут возникнуть ситуации,что нужно преобразовать поле в другой тип данных,например 
в коллекцию и наоборот.Тогда в аннотацию **@Mapping** следует добавить еще одно поле:
**qualifiedByName**, которое будет содержать имя метода, реализующего логику получения
нужного типа или значения. В таком случае этот метод должен быть помечен аннотацией
**@Named** c указанием названия для маппинга.
Ниже приведён общий пример:

````
{@Mapping(source = "chatDto.title", target = "title")
    @Mapping(source = "chatDto.image", target = "image")
    @Mapping(source = "userId",target ="users",qualifiedByName = "userIdToSet")
    public abstract GroupChat chatDtoToGroupChat(ChatDto chatDto,Long userId); }"
   

@Named("userIdToSet")
    public  Set<User> userIdToSet(Long userId) {
        User user = userService.getById(userId);
        Set<User> userSet = new HashSet<>();
        userSet.add(user);
        return userSet;
    }


