## QuizApp

![image](https://user-images.githubusercontent.com/46634713/122102595-e239b580-ce15-11eb-89b0-a4a3410c247a.png)

App for creating quizzes and running them. You can be powerful adming or simple user :).

#### Used technology:
- JavaFX,
- JDBC+SQLite,
- Apache Log4j 2.

## How to play ?

#### Admin
- login: root, password: toor.  
- as admin you can only manage question base. You can add a question, delete one with specific id or clear whole database.

Question structure:
- question,
- option A-D,
- correct answer (only one!).

#### User
- new users have to create an account.  
- after logging in you can start the quiz or check your latest result.  
- quiz can be only started if database contains at least two questions. 

#### Quiz
- order of questions and answers is random, 
- user has to select answer before going to the next question,
- quiz can be finished earlier, but the result isn't saved,
- after answering last question, summary page shows up with final result and table with correct answers.

