# Projeto Prático Avaliativo 1 - Dispositivos Móveis 2

**Disciplina:** Dispositivos Móveis 2  
**Aluno:** Bruno Ferreira

> Projeto Android desenvolvido como parte do curso de Analise e Desenvolvimento de Sistemas no IFSP na matéria de Dispositivos Móveis 2

---

## Descrição

Aplicativo Android desenvolvido em Kotlin com o objetivo de simular uma micro rede social, permitindo o compartilhamento de fotos, textos e localização entre usuários.
O app conta com autenticação de usuários, criação de perfil, publicação de posts com imagem e localização automática via GPS, feed paginado e busca por cidade.
---

## Funcionalidades

- Autenticação com e-mail e senha via Firebase Authentication
- Cadastro de usuário com nome completo, e-mail e senha
- Tela de perfil com foto, username e nome completo
- Edição de perfil, incluindo troca de senha
- Criação de posts com imagem da galeria, descrição e cidade obtida via GPS
- Feed de posts paginado (5 por vez) carregado do Firebase Firestore
- Busca de posts pelo nome da cidade
- Redirecionamento automático para a tela principal se o usuário já estiver logado

---

## Demonstração

[Vídeo demonstração](https://drive.google.com/file/d/18WSNM8CPfrIpYmzhmrGROiO9nlDTcoIC/view?usp=drive_link)
[Vídeo Explicação](https://drive.google.com/file/d/1zelHnIWvISDMoL2Jhum98CN4QbIfgDU_/view?usp=drive_link)

---

## Tecnologias Utilizadas

- Kotlin
- Android Studio
- Firebase Authentication
- Firebase Firestore
- View Binding
- RecyclerView e Adapter
- Fused Location Provider (GPS)
- Geocoder (conversão de coordenadas em cidade)
- Base64 para armazenamento de imagens
- Paginação por cursor com Timestamp

---

## Status do Projeto

Concluído — protótipo funcional com autenticação, perfil, criação de posts, feed paginado, busca por cidade e geolocalização automática.

---

## Tecnologias utilizadasc

Linguagem de programação Kotlin aplicada no software Android Studio

---

## Aprendizados e Desafios

- Integração com Firebase Authentication e Firestore
- Implementação de paginação por cursor usando Timestamp
- Uso do Fused Location Provider para obter localização em tempo real
- Geocodificação reversa para converter coordenadas em nome de cidade
- Armazenamento de imagens em Base64 no Firestore
- Organização do projeto seguindo boas práticas com separação em pacotes
- Integração do projeto com GitHub e documentação via README

---

## Instalação

git clone https://github.com/Brunoo00/MicroRedeSocial
