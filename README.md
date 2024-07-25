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

1. **Create a bot on Telegram**:
    - Open the Telegram app and search for `BotFather`.
    - Start a chat with `BotFather` and use the command `/newbot` to create a new bot.
    - Follow the instructions to set up the bot and obtain the bot token.

2. **Add the bot token to your configuration**:
    - Open the `application.yaml` file in your project.
    - Add the obtained bot token to the file:
      ```yaml
      telegram:
          token: YOUR_TELEGRAM_BOT_TOKEN
      ```
3. **Obtain a token for ChatGPT API**:
    - Visit the [OpenAI API page](https://beta.openai.com/signup/) and sign up or log in.
    - Generate an API token for ChatGPT.

4. **Add the ChatGPT API token to your configuration**:
    - Open the `application.yaml` file in your project.
    - Add the obtained ChatGPT API token to the file:
      ```yaml
      ai:
        token: YOUR_CHATGPT_API_TOKEN
      ```
5. **Run the application**:
    - Start the application in your development environment.

6. **Find and start using your bot on Telegram**:
    - Open Telegram and search for the bot you created.
    - Start a chat with the bot and send the `/start` command.
    - Choose the interview mode:
        - **QuizMode**: Questions with multiple-choice answers.
        - **InteractiveMode**: Questions without multiple-choice options, with analysis and feedback.
