*******************
** Configuration **
*******************

Dans le fichier config.ini, renseigner:
    - racf: votre racf
    - sshKey: chemin vers la clé privée SSH (format OpenSSH)
    - downloadDir: chemin vers le répertoire où télécharger les fichiers.
	
	
***************
** Lancement **
***************

L'application nécessite Java 8 ou supérieur.

** Utilisation du launcher "jeha-browser.exe" **
Le launcher ne fonctionne que si les chemins et versions de Java sont bien renseignés dans les bases de registre.
Il est très probable que cette configuration ne soit pas faite et l'exe ne fonctionnera pas.
Dans ce cas, se référer à l'autre mode de lancement.
	
** Utilisation du raccourci "Jeha! Browser" **
Par défaut, l'application doit se trouver dans le répertoire C:\PACKDEV\Jeha-Browser, Java 8 doit se trouver dans C:\PACKDEV\jdk1.8.0_45
Si ce n'est pas le cas, il est nécessaire de modifier le chemin vers Java et vers le jar Jeha-SCPBrowser.jar pour pouvoir le lancer.
    exemple: C:\Windows\System32\cmd.exe /c "SET PATH=C:\PACKDEV\jdk1.8.0_45\bin;%PATH%  && cd ^"C:\PACKDEV\Jeha-Browser^"  && java -jar Jeha-SCPBrowser.jar"
	
	
***************
** Exécution **
***************

** Démarrage **
Au démarrage, une fenêtre demande la saisie de la passphrase SSH.
A la validation, l'application tente de se connecter à localhost.
Si la passphrase n'est pas bonne ou que le poste n'est pas configuré correctement (config Cygwin), la fenêtre réapparait.

** Connexion **
Dans l'arbre des sessions, un double-clic sur un serveur permet d'ouvrir une connexion.
Pour ajouter une connexion (bouton + dans la barre d'outil), chaque caractère "/" renseigné dans le nom de la nouvelle connexion permet d'ajouter une branche
dans l'arbre des sessions (ex: VAL/WS/souscription)

** Navigation **
Dans la table de navigation, un double-clic sur un répertoire permet de s'y déplacer.
Il est possible de renseigner le chemin complet d'un répertoire dans la barre de navigation supérieure.

** Téléchargement de fichiers **
Un menu est disponible par clic droit sur un fichier et permet de lancer le téléchargement.
Le fichier est placé dans la liste des transferts en cours.
Une fois terminé, le fichier est placé dans la liste correspondant à son statut (success ou failure).
En cas de succès, le fichier est situé dans le répertoire "downloadDir" renseigné dans la configuration.

** Chargement vers le serveur **
Le bouton "Upload file" (Flèche bleue dans la barre d'outils) permet d'ouvrir une fenêtre de sélection de fichier a fin de charger un fichier vers le répertoire courant du serveur.
Il n'est possible de selectionner qu'un seul fichier à la fois.
Après validation, le fichier est placé dans la liste des transferts en cours.
Une fois terminé, le fichier est placé dans la liste correspondant à son statut (success ou failure).