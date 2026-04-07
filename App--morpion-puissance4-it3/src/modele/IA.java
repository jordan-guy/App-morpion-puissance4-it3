package modele;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import modele.Puissance4;

public class IA  extends Joueur{
    private Random rand = new Random();

    //constructeur IA
    public IA(){
        super("IA",'O');
    }

    public String choisirCoupMorpionNaif(Morpion morpion) {
        List<int[]> coupPossible = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (morpion.getGrille()[i][j]==' ') {
                    coupPossible.add(new int[]{i,j});
                }
            }
        }
        return coordonneeVersString(coupPossible.get(rand.nextInt(coupPossible.size())));
    }

    public String choisirCoupMorpionDifficile(Morpion jeu, int casesVides) {
        char[][] grille = jeu.getGrille();

        if (casesVides > 4) {
            //Heuristique simple : Jouer au centre, sinon un coin, sinon aléatoire.
            //Si l'IA peut gagner, elle joue le coup gagnant.
            int[] coup = chercherCoupDecisif(grille,'O');
            if (coup != null) return coordonneeVersString(coup);

            //Si le joueur peut gagner l'IA le bloque.
            coup = chercherCoupDecisif(grille,'X');
            if (coup != null) return coordonneeVersString(coup);

            //si le centre est libre
            if (grille[1][1]==' ') return "22";

            //pour jouer dans les coins
            int[][] coins = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
            for (int[] c : coins)
                if (grille[c[0]][c[1]] == ' ')
                    return coordonneeVersString(c);

            //pour jouer sur les côtés
            int[][] cotes = {{0, 1}, {1, 0}, {1, 2}, {2, 1}};
            for (int[] c : cotes)
                if (grille[c[0]][c[1]] == ' ')
                    return coordonneeVersString(c);
        }
        return minmaxMorpion(grille);
    }

    private String minmaxMorpion(char[][] grille) {
        //Initialiser meilleurScore avec la valeur int la plus petite possible
        //Initialiser meilleurCoup comme une liste vide
        int meilleurScore = Integer.MIN_VALUE;
        int[] meilleurCoups = null;

        for (int i=0;i<3;i++) {
            for (int j=0;j<3;j++) {
                if (grille[i][j]==' '){
                    grille[i][j]='O';
                    int score = calculMinMaxMorpion(grille, false); //lance la fonction récursive pour trouver le meilleur score
                    grille[i][j]=' ';

                    //mets à jour le meilleurScore
                    if (score>meilleurScore) {
                        meilleurScore = score;
                        //enregistre le coup dans meilleurCOups
                        meilleurCoups=new int[]{i,j};
                    }
                }
            }
        }
        return coordonneeVersString(meilleurCoups);
    }

    //calcul la valeur du meilleur coup que l'IA peut jouer en minimisant les coups du joueur
    private int calculMinMaxMorpion(char[][] grille, boolean tour_IA) {
        //L'IA gagne un point pour la win
        if (aGagneMorpion(grille,'O')) return 1;
        //L'IA perd un point pour la loose
        if (aGagneMorpion(grille,'X')) return -1;
        //Pas de point gagner pour l'égalité
        if (estPlein(grille)) return 0;

        if (tour_IA){
            //prend la valeur la plus petite possible pour meilleur
            int meilleur = Integer.MIN_VALUE;
            for (int i=0;i<3;i++) {
                for (int j=0;j<3;j++) {
                    if (grille[i][j]==' '){
                        grille[i][j]='O'; //joue un coup de l'IA pour tester le meilleur coup
                        int score = calculMinMaxMorpion(grille,false)+1; //appelle de la fonction récursive pour trouver le meilleur coup en ajoutant pour son meilleur coup
                        grille[i][j]=' '; //remet la case a vide après le test
                        meilleur=Math.max(meilleur,score); //choisie le score le plus grand pour être le meilleur
                    }
                }
            }
            return meilleur;
        }else{
            int meilleur = Integer.MAX_VALUE;
            for (int i=0;i<3;i++) {
                for (int j=0;j<3;j++) {
                    if (grille[i][j]==' '){
                        grille[i][j]='X'; //joue un coup pour le joueur pour tester le meilleur coup
                        int score = calculMinMaxMorpion(grille,true)-1; //appelle de la fonction récursive pour trouver le meilleur coup en enlevant pour son meilleur coup
                        grille[i][j]=' ';
                        meilleur=Math.min(meilleur,score);  //choisie le score le plus petit pour être le meilleur
                    }
                }
            }
            return meilleur;
        }
    }

    private boolean estPlein(char[][] grille) {
        for (int i=0;i<3;i++) {
            for (int j=0;j<3;j++) {
                if (grille[i][j]==' ') return false;
            }
        }
        return true;
    }

    private boolean aGagneMorpion(char[][] grille, char symbole) {
        for (int i=0;i<3;i++) {
            //Lignes
            if (grille[i][0]==symbole &&  grille[i][1]==symbole && grille[i][2]==symbole) return true;
            //Colonnes
            if (grille[0][i]==symbole &&  grille[1][i]==symbole && grille[2][i]==symbole) return true;
        }
        //Diagonales
        if (grille[0][0] == symbole && grille[1][1]== symbole && grille[2][2]== symbole) return true;
        if (grille[0][2] == symbole && grille[1][1]== symbole && grille[2][0]== symbole) return true;
        //Pas gagnés
        return false;
    }

    private int[] chercherCoupDecisif(char[][] grille, char symbole) {
        for (int i=0; i<3; i++) {
            // Lignes
            if (grille[i][0]==symbole && grille[i][1]==symbole && grille[i][2]==' ') return new int[]{i, 2};
            if (grille[i][0]==symbole && grille[i][2]==symbole && grille[i][1]==' ') return new int[]{i, 1};
            if (grille[i][1]==symbole && grille[i][2]==symbole && grille[i][0]==' ') return new int[]{i, 0};
            // Colonnes
            if (grille[0][i]==symbole && grille[1][i]==symbole && grille[2][i]==' ') return new int[]{2, i};
            if (grille[0][i]==symbole && grille[2][i]==symbole && grille[1][i]==' ') return new int[]{1, i};
            if (grille[1][i]==symbole && grille[2][i]==symbole && grille[0][i]==' ') return new int[]{0, i};
        }
        // Diagonales
        if (grille[0][0]==symbole && grille[1][1]==symbole && grille[2][2]==' ') return new int[]{2, 2};
        if (grille[0][2]==symbole && grille[1][1]==symbole && grille[2][0]==' ') return new int[]{2, 0};
        return null;
    }

    //transforme les coordonnées vers un String
    private String coordonneeVersString(int[] c) {
        return "" + (c[0] + 1) + (c[1] + 1);
    }

    // IA naif pour puissance4
    public int choisirCoupPuissance4Naif(Puissance4 jeu) {
        List<Integer> colonnesValides = new ArrayList<>();
        for (int c = 0; c < 7; c++) {
            // On vérifie si la colonne n'est pas pleine (en regardant la ligne 0).
            if (jeu.obtenirGrille().contains("|.")) {
                colonnesValides.add(c + 1);
            }
        }
        return colonnesValides.get(rand.nextInt(colonnesValides.size()));
    }

    public int choisirCoupPuissance4Difficile(Puissance4 jeu) {
        char[][] grille = jeu.getGrille();
        int[]score = new int[7];

        for  (int c = 0; c < 7; c++) {
            int ligne=trouverLigne(grille,c);
            if (ligne==-1) continue;

            //Defensive
            grille[ligne][c]='R';
            if (aGagnePuissance4(grille,'R')) score[c]+=900;
            grille[ligne][c]='.';

            //Offensive
            grille[ligne][c]='J';
            //L'Ia peut aligne 4 pions +1000
            if (aGagnePuissance4(grille,'J')) score[c]+=1000;

            //L'Ia peut aligner 3 pions +100 par espace vide à côté
            score[c]+=nbpions(grille,ligne,c,'J',3)*100 ;

            //L'Ia peut aligner 2 pions +10 par groupe de deux espaces (pas de doublons AB=BA).
            score[c]+=nbpions(grille,ligne,c,'J',2)*10 ;

            grille[ligne][c]='.';
            score[c]+=bonus(c);
        }

        int scoreMax = Integer.MIN_VALUE;
        for (int c = 0; c < 7; c++) {
            if (trouverLigne(grille,c)!= -1 && score[c]>scoreMax) {
                scoreMax = score[c];
            }
        }

        List<Integer> meilleur = new ArrayList<>();
        for (int c = 0; c < 7; c++) {
            if (trouverLigne(grille,c) != -1 && score[c]==scoreMax) {
                meilleur.add(c+1);
            }
        }
        return meilleur.get(rand.nextInt(meilleur.size()));
    }

    private int trouverLigne(char[][] grille, int c) {
        for (int i=5; i>=0; i--) {
            if (grille[i][c]=='.') return i;
        }
        return -1;
    }

    private boolean aGagnePuissance4(char[][] grille, char symbole) {
        //Vertical
        for (int i = 0; i <=2 ; i++) {
            for (int j = 0; j <7 ; j++) {
                if (grille[i][j]==symbole && grille[i+1][j]==symbole && grille[i+2][j]==symbole && grille[i+3][j]==symbole) return true;
            }
        }

        //Horizontal
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j <=3 ; j++) {
                if (grille[i][j]==symbole && grille[i][j+1]==symbole && grille[i][j+2]==symbole && grille[i][j+3]==symbole) return true;
            }
        }

        //Diagonale
        //Diagonale ( \ )
        for (int i=0; i<=2 ; i++) {
            for (int j=0; j<=3 ; j++) {
                if (grille[i][j]==symbole && grille[i+1][j+1]==symbole && grille[i+2][j+2]==symbole && grille[i+3][j+3]==symbole) return true;
            }
        }

        //Diagonale ( / )
        for (int i = 0; i <2; i++) {
            for (int j = 3; j <=6 ; j++) {
                if (grille[i][j]==symbole && grille[i+1][j-1]==symbole && grille[i+2][j-2]==symbole && grille[i+3][j-3]==symbole) return true;
            }
        }
        return false;
    }

    private int bonus(int c){
        int[]bonus ={1,2,3,5,3,2,1};
        return bonus[c];
    }

    private int nbpions(char[][] grille,int ligne,int c, char symbole,int longueur){
        int compteur=0;
        int[][] direction= {{0,1},{1,0},{1,1},{1,-1}};

        for (int[]dir : direction) {
            int dl = dir[0];
            int dc = dir[1];

            for (int depart=-longueur; depart<=0; depart++) {
                int pions=0;
                int vides=0;
                boolean caseOccuper=false;

                for (int i=0;i<=longueur;i++) {
                    int l= ligne+ (depart+i)*dl;
                    int co=c+(depart+i)*dc;

                    //Si c'est hors grille
                    if (l<0 || l>=6 || co<0 || co>=7) {
                        pions=0;
                        break;
                    }

                    if (grille[l][co]==symbole) pions++;
                    else if (grille[l][co]=='.') vides++;
                    else {
                        caseOccuper=true;
                        break;
                    }
                }
                if (pions==longueur && !caseOccuper && vides==1) compteur++;
            }
        }
        return compteur;
    }
}