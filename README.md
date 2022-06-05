# java-spring-crud-mvc-sample

Este repositório contém um exemplo básico de implementação de API em Java com Spring Boot e foi criado para propósitos didáticos.
## Negócio
A implementação atende à seguinte estória de usuário:
- **Como** usuário
- **Quero** manter um cadastro de endereços de e-mails dos meus contatos por meio de uma API
- **Para** listá-los quando eu precisar usá-los

**Critérios de aceite**
- Um mesmo endereço de e-mail não pode pertencer a dois contatos
- Um identificador único deve ser gerado para cada registro de contato
## Aspectos técnicos
### Topologia
Os pacotes foram organizados da seguinte forma:
- **models**: Estruturas de dados para objetos de entrada e saída (DTO - Data Transfer Objects).
- **domains**: Interfaces com as operações disponibilizadas para cada domínio.
- **services**: Implementações das operações de domínio (pacote "domains"). Regras de Negócio.
- **entities**: Estruturas de dados para entidades do banco de dados.
- **repositories**: Interfaces com operações de entidades com o banco de dados.
- **exceptions**: Exceções usadas nas regras de negócio.
### Execução
#### Docker (via repositório remoto)
A cada commit uma nova imagem docker será criada no repositório que pode ser acessado no link abaixo. O identificador do commit será usado como tag da imagem gerada. https://hub.docker.com/repository/docker/marcelomotaribeiro/java-spring-crud-mvc-sample
##### Comandos (usando o commit f5ac6c8, como exemplo):

`$ docker pull marcelomotaribeiro/java-spring-crud-mvc-sample:f5ac6c8`

`$ docker run -dp 8080:8080 marcelomotaribeiro/java-spring-crud-mvc-sample:f5ac6c8`

#### Docker (local)
##### Comandos (Requer Java 11, Maven e Docker instalados. Devem ser executados na pasta raiz do projeto)

`$ mvn package`

`$ docker build . --file Dockerfile --tag marcelomotaribeiro/java-spring-crud-mvc-sample:latest`

`$ docker run -dp 8080:8080 marcelomotaribeiro/java-spring-crud-mvc-sample:latest`

#### Java (local)
##### Comandos (Requer Java 11 e Maven instalados. Devem ser executados na pasta raiz do projeto)

`$ mvn clean install`

`$ java -jar target/java-spring-crud-mvc-sample-0.0.1-SNAPSHOT.jar`

URL padrão (será exibido o swagger): http://localhost:8080
