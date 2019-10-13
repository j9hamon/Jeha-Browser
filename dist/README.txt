*******************
** Configuration **
*******************

Dans le fichier config.ini, renseigner:
    - racf: votre racf
    - sshKey: chemin vers la cl� priv�e SSH (format OpenSSH)
    - downloadDir: chemin vers le r�pertoire o� t�l�charger les fichiers.
	
	
***************
** Lancement **
***************

L'application n�cessite Java 8 ou sup�rieur.

** Utilisation du launcher "jeha-browser.exe" **
Le launcher ne fonctionne que si les chemins et versions de Java sont bien renseign�s dans les bases de registre.
Il est tr�s probable que cette configuration ne soit pas faite et l'exe ne fonctionnera pas.
Dans ce cas, se r�f�rer � l'autre mode de lancement.
	
** Utilisation du raccourci "Jeha! Browser" **
Par d�faut, l'application doit se trouver dans le r�pertoire C:\PACKDEV\Jeha-Browser, Java 8 doit se trouver dans C:\PACKDEV\jdk1.8.0_45
Si ce n'est pas le cas, il est n�cessaire de modifier le chemin vers Java et vers le jar Jeha-SCPBrowser.jar pour pouvoir le lancer.
    exemple: C:\Windows\System32\cmd.exe /c "SET PATH=C:\PACKDEV\jdk1.8.0_45\bin;%PATH%  && cd ^"C:\PACKDEV\Jeha-Browser^"  && java -jar Jeha-SCPBrowser.jar"
	
	
***************
** Ex�cution **
***************

** D�marrage **
Au d�marrage, une fen�tre demande la saisie de la passphrase SSH.
A la validation, l'application tente de se connecter � localhost.
Si la passphrase n'est pas bonne ou que le poste n'est pas configur� correctement (config Cygwin), la fen�tre r�apparait.

** Connexion **
Dans l'arbre des sessions, un double-clic sur un serveur permet d'ouvrir une connexion.
Pour ajouter une connexion (bouton + dans la barre d'outil), chaque caract�re "/" renseign� dans le nom de la nouvelle connexion permet d'ajouter une branche
dans l'arbre des sessions (ex: VAL/WS/souscription)

** Navigation **
Dans la table de navigation, un double-clic sur un r�pertoire permet de s'y d�placer.
Il est possible de renseigner le chemin complet d'un r�pertoire dans la barre de navigation sup�rieure.

** T�l�chargement de fichiers **
Un menu est disponible par clic droit sur un fichier et permet de lancer le t�l�chargement.
Le fichier est plac� dans la liste des transferts en cours.
Une fois termin�, le fichier est plac� dans la liste correspondant � son statut (success ou failure).
En cas de succ�s, le fichier est situ� dans le r�pertoire "downloadDir" renseign� dans la configuration.

** Chargement vers le serveur **
Le bouton "Upload file" (Fl�che bleue dans la barre d'outils) permet d'ouvrir une fen�tre de s�lection de fichier a fin de charger un fichier vers le r�pertoire courant du serveur.
Il n'est possible de selectionner qu'un seul fichier � la fois.
Apr�s validation, le fichier est plac� dans la liste des transferts en cours.
Une fois termin�, le fichier est plac� dans la liste correspondant � son statut (success ou failure).