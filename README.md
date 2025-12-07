# 💣 Cannon Game - Projeto Final de Programação III

Aplicação desenvolvida como projeto final da disciplina de **Programação III**, baseada no Capítulo 6 do livro **Android 6 for Programmers: An App-Driven Approach (3rd Edition)**. O projeto implementa os conceitos fundamentais de desenvolvimento de jogos 2D para Android, incluindo manipulação gráfica, threads, sons e interatividade.

---

## 👥 Integrantes do Grupo

- Ana Flávia Alves Rosa
- Beatriz da Costa Lauro
- Brenda Bonaita de Oliveira
- Leticia Gomes dos Santos
- Liniker Silva

---

## 🎯 Objetivo do Projeto

Desenvolver a aplicação completa **Cannon Game**, aplicando de forma prática os conceitos de animação, manipulação gráfica em baixo nível, controle de threads, execução de sons e interatividade por toque, conforme ensinado na disciplina e detalhado no livro de referência.

---

## 🧩 Funcionalidades Implementadas
### ✅ Funcionalidades Obrigatórias

- **Animação e Game Loop**: O jogo roda em uma thread dedicada (`CannonThread`) que gerencia o loop principal, atualizando o estado e redesenhando a tela a uma taxa de aproximadamente 60 FPS.
- **Gráficos com Canvas e Paint**: Todos os elementos visuais (canhão, alvos, bloqueador, bola) são desenhados manualmente na tela utilizando as classes `Canvas` e `Paint`.
- **Execução de Sons**: O `SoundPool` é utilizado para carregar e reproduzir efeitos sonoros de forma eficiente durante o jogo.
- **Controle de Threads**: A lógica do jogo é separada da UI thread, garantindo uma experiência fluida e responsiva.
- **SurfaceView e SurfaceHolder**: A renderização é feita em uma `SurfaceView`, permitindo que uma thread secundária desenhe na tela de forma segura.
- **Modo Imersivo**: A aplicação entra em modo de tela cheia, ocultando as barras de sistema para uma imersão completa.
- **Interatividade por Toque**: O jogador mira e atira com o canhão através de eventos de toque (`ACTION_DOWN` e `ACTION_MOVE`).

### ✨ Funcionalidade Extra Implementada

- **Novos Sons e Efeitos Sonoros Customizados**: Para melhorar a experiência do jogador, foram implementados três efeitos sonoros distintos, que vão além do requisito mínimo:
  1. **Som de Disparo**: Um som (`cannon_fire.wav`) é executado sempre que o canhão atira.
  2. **Som de Acerto no Alvo**: Um som (`target_hit.wav`) é tocado quando a bola de canhão atinge um alvo.
  3. **Som de Colisão com Bloqueador**: Um som (`blocker_hit.wav`) é reproduzido quando a bola colide com o obstáculo.

---

## ▶️ Como Executar

1. **Clone o repositório** para sua máquina local.
2. **Abra o projeto** na IDE Android Studio.
3. **Aguarde** o Gradle sincronizar e construir o projeto.
4. **Execute a aplicação** em um emulador Android (API 21 ou superior) ou em um dispositivo físico.

O projeto deve compilar e executar sem a necessidade de configurações adicionais.

---

## 📸 Screenshots

![Demonstração do Cannon Game](assets/exemplo.gif)


---

## 🗂️ Estrutura do Código

O código-fonte está organizado de forma modular, seguindo as boas práticas de desenvolvimento Android:

- **`MainActivity` e `MainActivityFragment`**: Controlam o ciclo de vida da tela principal e do fragmento que contém o jogo.
- **`CannonView`**: Classe central que herda de `SurfaceView` e gerencia toda a lógica do jogo, incluindo a thread principal, o desenho dos elementos e a detecção de colisões.
- **`GameElement`**: Classe base abstrata para todos os elementos do jogo (alvos e bloqueador).
- **`Cannon`, `Cannonball`, `Target`, `Blocker`**: Classes que representam cada um dos elementos interativos do jogo, com seus respectivos comportamentos e atributos.
- **`/res/raw`**: Contém os arquivos de áudio (`.wav`) utilizados no jogo.
- **`/res/layout`**: Define a estrutura visual da Activity e do Fragment.

---

*Projeto desenvolvido para a disciplina de Programação III do 6° Período do curso de Sistemas de Informação da Universidade do Estado de Minas Gerais (UEMG).*
