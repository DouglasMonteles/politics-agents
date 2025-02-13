# Pré-requisitos:

## Tecnologias:
- Java 21
- Intellij IDE (opcional)

## Baixe os dados de treino

Esses dados podem ser baixados pelo sistema, mas levam bastante tempo para terminar o download. Como alternativa, é possível baixar diretamente o .zip contendo os dados:

- deputados;
- partidos;
- proposicoes;
- votacoes;

O link para download está [aqui - votes-proposals-data.zip](https://github.com/DouglasMonteles/politics-agents-external-files/blob/main/votes-proposals-data.zip)

Depois que baixar, descompacte os dados no módulo: `external-data-processor`, em específico, no caminho: `external-data-processor/src/main/resources`. O diretório deve ficar assim:

![image](https://github.com/user-attachments/assets/afdce624-1319-4b55-b9ae-479b26f74d8e)

## Baixe os Modelos Treinados

Caso deseje executar a aplicação diretamente, sem a necessidade de treinar os modelos, é possível baixar os modelos já treinados dos testes que foram realizados:

- [Modelos do teste 1](https://github.com/DouglasMonteles/politics-agents-external-files/blob/main/trained-data-scenario1.zip)
- [Modelos do teste 2](https://github.com/DouglasMonteles/politics-agents-external-files/blob/main/trained-data-scenario2.zip)
- [Modelos do teste 3](https://github.com/DouglasMonteles/politics-agents-external-files/blob/main/trained-data-scenario3.zip)

Descompacte esse diretório na raiz do projeto. O nome do diretório não pode ser alterado. 

![image](https://github.com/user-attachments/assets/0e7efe57-6aff-4801-95ae-8b7fc5adbbd2)


# Como executar o projeto

## Utilizando Makefile

Abra o terminal na raiz do projeto e execute:

```
make build-and-run
```

## Utilizando a IDE

Execute a classe principal localizada em:

```
agents/src/main/java/org/fga/tcc/AgentBootApplication.java
```

Ao executar o projeto, duas janelas serão exibidas, a janela do JADE e a janela da aplicação. Foque na da aplicação, e clique em: "Simulação de Votação" -> "Selecione a proposição" -> Visualize a simulação
