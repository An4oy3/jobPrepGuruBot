# jobPrepGuruBot

jobPrepGuruBot is a Telegram bot designed to assist with technical interview preparation. It offers two modes for practice:

1. **QuizMode** - Standard quiz mode where users are presented with questions and multiple-choice answers.
2. **InteractiveMode** - Interactive mode where users are asked questions without multiple-choice options. Users input their answers in text form, which are then analyzed by ChatGPT to provide feedback.

## Installation

To run the application in a development environment, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/An4oy3/jobPrepGuruBot.git
    cd jobPrepGuruBot
    ```

2. Ensure you have Docker and Docker Compose installed.

3. Start the services using docker-compose to set up the containers (guru_container(database) & admin_container):
    ```sh
    docker-compose up
    ```

## Usage

1. Run the application in your development environment.

2. Open Telegram and find the bot named jobPrepGuruBot.

3. Send the `/start` command in the chat with the bot.

4. Choose the interview mode:
    - **QuizMode**: Questions with multiple-choice answers.
    - **InteractiveMode**: Questions without multiple-choice options, with analysis and feedback.
