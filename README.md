# SpoonHAI913I - Analyseur de Code Java avec Spoon

### 1. Compilation
```cmd
mvn clean compile
```

### 3. Interface Web (Spring Boot)
```cmd
mvn spring-boot:run
```
Puis ouvrir : http://localhost:8080/

Ensuite entrer le chemin absolu du dossier à analyser (plusieurs peuvent être ajouter via le bouton +)

Entrer la référence des fichiers à exclure de l'analyse (plusieurs peuvent être ajouter via le bouton +)

Choisir ensuite le type d'analyse voulu, "Tout" les effectuant toutes.

En cas de choix de "Tout" ou "Statistiques", choisir une valeur de X, permettant d'obtenir les classes ayant plus de X méthodes.

En cas de choix de "Tout" ou "Modules", choisir une valeur de CP, permettant de définir le seuil de validation des modules.


