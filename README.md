
## 📡 WifiMap  
 

### 📝 Description  :  

Ce projet vise à développer une application permettant de visualiser la couverture WiFi sous forme de carte thermique. L’application prend en compte les barrières physiques (murs, meubles, matériaux de construction) et l’emplacement des points d’accès (AP) afin d’offrir une représentation réaliste de la portée et de la force du signal WiFi.

### 🎯 Objectifs  :  

Analyser et modéliser la couverture WiFi d’un environnement connecté.
Prendre en compte les obstacles physiques influençant le signal.
Générer une carte thermique pour visualiser l’intensité du signal.
Appliquer les principes du génie logiciel et de la programmation orientée objet.
Utiliser UML pour la conception du système.


### 🛠️ Technologies utilisées  :  

Langage : Java  
Interface utilisateur : Java Swing  
Modélisation : UML  
Gestion de version : GitLab  


### 🏗️ Équipe de développement  :  
  
  Mohamed Ch (Team Lead)
  Ismail
  Djordje  
  Mamadou  
  Raphaël  
    

### Guide d'utilisation

### Créer un nouveau projet

1. Allez sur fichier -> Nouveau projet
2. Changez la largeur et l'hauteur
3. Cliquez sur Créer

#### Sélection de mode

Pour mode de *sélection*, cliquez sur l'icône de curseur sur la barre d'outil horizontale. Pour ajouter des objets, sélectionnez l'icône correspondante.

#### Créer un point d'accès

##### Par le panneau d'édition

1. Remplissez le formulaire du panneau d'édition, tout en vous assurant de ne pas avoir de point d'accès de sélectionné 
2. Cliquez sur Appliquez

##### Par la souris

1. Cliquez sur la map avec votre souris avec la mode Point accès de sélectionné

#### Créer une barrière

##### Par le panneau d'édition

1. Remplissez le formulaire du panneau d'édition, tout en vous assurant de ne pas avoir de barrière de sélectionné 
2. Cliquez sur Appliquez

##### Par la souris

1. Sélectionnez l'outil de barrière
2. Cliquez une fois sur le point de départ voulu sur la map
3. Sans appuyer sur un bouton, faites glisser votre curseur vers votre point de fin voulu

#### Déplacer un point d'accès

##### Par le panneau d'édition

1. Sélectionnez l'outil de sélection
2. Sélectionnez un point d'accès avec la souris, en cliquant sur un point rouge
3. Entrez la position voulue X et Y dans les champs dans le panneau d'édition (X-Axis et Y-Axis)
4. Cliquez sur Appliquer

##### Par la souris

1. Sélectionnez l'outil de sélection
2. Cliquez sur le point d'accès
3. En tenant le bouton gauche enfoncé, faites glisser le curseur vers l'endroit désiré

#### Déplacer une barrière

##### Par le panneau d'édition

1. Sélectionnez l'outil de sélection
2. Sélectionnez une barrière avec la souris, en cliquant sur la barrière
3. Entrez la position de départ X ou Y, ou fin X ou Y dans le panneau d'édition
4. Cliquez sur Appliquer

##### Par la souris

1. Sélectionnez l'outil de sélection
2. Cliquez sur la barrière
3. En tenant le bouton gauche enfoncé, faites glisser le curseur vers l'endroit désiré

#### Modifier la puissance et la fréquence d'un point d'accès

1. Sélectionnez l'outil de sélection
2. Sélectionnez un point d'accès avec la souris
3. Changez ses paramètres puissance ou fréquence dans le panneau d'édition
4. Cliquez sur appliquez

#### Modifier les paramètres d'une barrière

1. Sélectionnez l'outil de sélection
2. Sélectionnez une barrière avec la souris
3. Changez ses paramètres dans le panneau d'édition
4. Cliquez sur appliquez

#### Supprimer un point d'accès/une barrière

1. Sélectionnez l'outil de sélection
2. Sélectionnez un point d'accès ou une barrière avec la souris
3. Cliquez sur Supprimer

#### Zoom

1. Mettre la souris où vous voulez effectuer un zoom
2. Faites défiler la molette de votre souris vers l'avant (zoom avant) ou l'arrière (zoom arrière)

#### Exporter une image

1. Créer un projet
2. Cliquez sur Fichier
3. Cliquez sur Exporter l'image

#### Sauvegarder

##### Sauvegarde rapide

1. Fichier -> Enregistrer le projet OU CTRL + s

##### Sauvegarde complète

1. Fichier -> Enregistrer le projet sous OU CTRL + MAJ + s

#### Ouvrir un projet

1. Fichier -> Ouvrir un projet
2. Sélectionnez un .ser

#### Annuler/Rétablir

CTRL + z pour annuler ou CTRL + MAJ + z pour rétablir

OU

Cliquez sur une des flèches dans la barre d'outil

OU

Édition -> Annuler/Rétablir

#### Changer les paramètres de la grille

1. Utilisez les options dans le panneau de configuration, section "Configuration Grille". Vous aurez seulement à cliquer sur appuyer pour l'entrée de Taille

#### Bonus:

- Un indicateur d'état de couverture se trouve à la gauche de la légende thermique. Il indique OK si toute la map, dans son entièreté, est au minimum -59 dBm. Sinon, il indique PAS OK
- Un option pour l'effet thermique est fournie. Choisissez entre la mode Normal, Chaud, ou Froid pour changer un peu le rendu de la carte thermique.
