Tobias Lepoutre (20177637)
Abderrahmane Manseur (20148685)

Lien vers notre repositoire: https://github.com/Infamous2078/IFT3913_devoir1

Documentation:
1) Compiler les 4 fichiers .java via la commande:
javac Tloc.java Tassert.java Tls.java Tropcomp.java

2) Executer séparemment les 4 fichiers .jar:
java -jar Tloc.jar
java -jar Tassert.jar
java -jar Tls.jar
java -jar Tropcomp.jar

3) Vous êtes à présent en mesure d'utiliser ces 4 fonctions grâce aux commandes décrites ci-dessous:

Obtenir le nombre de lignes de code dans un fichier (ni vide, ni commentaires):
java Tloc <fichier>

Obtenir le nombre d'assertions dans un fichier:
java Tassert <fichier>

Obtenir les détails d'un repertoir (Chemin du fichier, Nom du paquet, Nom de la classe, Tloc de la classe, 
Tassert de la classe, Tcmp de la classe) et, optionnellement, les enregistrer dans un fichier.csv:
java Tls <fichier>
