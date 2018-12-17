/*
Zadanie 3
Podlancuch Alfa nazywamy powtorzonym prefiksem lancucha S, jesli Alfa jest prefiksem
S oraz ma on postac BetaBeta dla pewnego lancucha Beta. Zaproponuj i zaimplementuj
liniowy algorytm znajdujacy najdluzszy powtorzony prefiks dla zadanego lancucha S.
  */

//Definicje:
//s - tekst, ciąg symboli s = s1,s2,...sn nalezacych do alfabetu
//n - Dlugosc tekstu (liczba jego elementów)
//p - pattern (wzorzec)

import java.util.*;


public class SuffixTree {

    static String ALPHABET = "";
    static CharSequence s = "";

    public static class Node {

        int begin;
        int end;
        int depth; //distance in characters from root to this node
        Node parent;
        Node suffixLink;

        Map<Character, Node> children;  //zamiast Node[] children
        int numberOfLeaves;             //zliczamy liscie

        Node(int begin, int end, int depth, int noleaf, Node parent) {

            this.begin = begin;
            this.end = end;
            this.depth = depth;
            this.parent = parent;
            children = new HashMap<>();
            numberOfLeaves = noleaf;


        }
    }

    private static Node buildSuffixTree(CharSequence s) {


        //return_s(s.toString());
        SuffixTree.s = s;

        int n = s.length();
        byte[] a = new byte[n];

        for (int i = 0; i < n; i++) {
            a[i] = (byte) ALPHABET.indexOf(s.charAt(i));
        }

        Node root = new Node(0, 0, 0, 0, null);
        Node node = root;

        for (int i = 0, tail = 0; i < n; i++, tail++) {

            //ustaw ostatni stworzony węzeł wewnętrzny na null przed rozpoczęciem każdej fazy.
            Node last = null;

            //tail - tyle sufiksów musi zostać utworzone.
            while (tail >= 0) {
                Node ch = node.children.get(ALPHABET.charAt(a[i - tail]));
                while (ch != null && tail >= ch.end - ch.begin) {

                    //liscie
                    node.numberOfLeaves++;

                    tail -= ch.end - ch.begin;
                    node = ch;
                    ch = ch.children.get(ALPHABET.charAt(a[i - tail]));
                }

                if (ch == null) {
                    // utworz nowy Node z bieżącym znakiem
                    node.children.put(ALPHABET.charAt(a[i]),
                            new Node(i, n, node.depth + node.end - node.begin, 1, node));

                    //liscie
                    node.numberOfLeaves++;

                    if (last != null) {
                        last.suffixLink = node;
                    }
                    last = null;
                } else {
                    byte t = a[ch.begin + tail];
                    if (t == a[i]) {
                        if (last != null) {
                            last.suffixLink = node;
                        }
                        break;
                    } else {
                        Node splitNode = new Node(ch.begin, ch.begin + tail,
                                node.depth + node.end - node.begin, 0, node);
                        splitNode.children.put(ALPHABET.charAt(a[i]),
                                new Node(i, n, ch.depth + tail, 1, splitNode));

                        //liscie
                        splitNode.numberOfLeaves++;

                        splitNode.children.put(ALPHABET.charAt(t), ch);

                        //liscie
                        splitNode.numberOfLeaves += ch.numberOfLeaves;

                        ch.begin += tail;
                        ch.depth += tail;
                        ch.parent = splitNode;
                        node.children.put(ALPHABET.charAt(a[i - tail]), splitNode);

                        //liscie
                        node.numberOfLeaves++;

                        if (last != null) {
                            last.suffixLink = splitNode;
                        }
                        last = splitNode;
                    }
                }
                if (node == root) {
                    --tail;
                } else {
                    node = node.suffixLink;
                }
            }
        }
        return root;
    }


    private static void print(CharSequence s, int i, int j) {
        for (int k = i; k < j; k++) {
            System.out.print(s.charAt(k));
        }
    }

    //Jesli chcemy wydrukowac drzewo nalezy odkomentowac w main
    private static void printTree(Node n, CharSequence s, int spaces) {
        int i;
        for (i = 0; i < spaces; i++) {
            System.out.print("␣");
        }
        print(s, n.begin, n.end);
        System.out.println("␣" + (n.depth + n.end - n.begin));

        for (Node child : n.children.values()) {
            if (child != null) {
                printTree(child, s, spaces + 4);
            }
        }

    }

    /*##########################################################################################*/

    //Przechodząc od pierwszej litery do liscia najdluzszego Suffix-u.
    // Ograniczamy s (text) dzieki (subSequence) od początku do głebokosci jaką ma ten lisc

    //Wyznaczamy w drzewie najglebszy wierzcholek(lisc) (najbardziej oddalony od korzenia)
    private static CharSequence depthLeafLongestSuffix(Node root) {
        Node actualNode = root;

        int index_s = 0;
        while (index_s < s.length()) {
            actualNode = actualNode.children.get(s.charAt(index_s));
            index_s = actualNode.end;
        }
        return s.subSequence(0, actualNode.depth);
    }

    // 1. Iterujemy po wyzej znalezionym tekscie od poczatku do polowy tekstu i sprawdzamy
    // czy te znaki pokrywaja sie z tymi od polowy do konca

    // 2. w  przypadku braku pokrywania zostaje usuniety ostatni znak

    // 3. Powtarzamy az skonczy sie tekst i nie znajdziemy powtorzonego prefiksu
    // lub nie bedzie trzeba usuwac ostatniego znaku, bo znalezlismy powtorzony prefiks

    // 4. Zwracamy ten najdluzszy powtorzony prefiks -  subSequence(0, Lenght_dLLS * 2)


    private static CharSequence Search_depthLeafLongestSuffix(CharSequence s, CharSequence dLLS) {

        int Lenght_dLLS = dLLS.length();

        do {

            dLLS = dLLS.subSequence(0, Lenght_dLLS);

            for (int dLLSIndex = 0, sIndex = Lenght_dLLS; sIndex < dLLS.length() * 2; sIndex++, dLLSIndex++)
            {
                if (dLLS.charAt(dLLSIndex) != s.charAt(sIndex))

                {
                    Lenght_dLLS--;
                    break;
                }
            }

        } while
        (Lenght_dLLS != dLLS.length());

        return s.subSequence(0, Lenght_dLLS * 2);

    }

    /*##########################################################################################*/

    // funkcja pomocnicza ustawiająca ALPHABET
    private static void saveAlphabet(String s) {
        final Set<Character> set = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            set.add(s.charAt(i));
        }

        StringBuilder alphabetS = new StringBuilder();
        for (char ch : set) {
            alphabetS.append(ch);
        }
        ALPHABET = alphabetS.toString();
    }


    //main - Test
    public static void main(String[] args) {

        try {

            // Test wynik to AniaAnia a nie AniaAniaAnia
            String s = "AniaAniazrobilemAniaAniaAniahuraAniaAniaAnia$";

            saveAlphabet(s);

            Node root = buildSuffixTree(s);

            //Jesli chcemy wydrukowac drzewo nalezy odkomentowac
            //printTree(root, s, 0);

            CharSequence depthLLS = depthLeafLongestSuffix(root);
            System.out.println(" ");
            System.out.println("Najdluzszy powtorzony prefiks dla tekstu s = " + s + " to: ");
            System.out.println(" ");
            System.out.println(Search_depthLeafLongestSuffix(s, depthLLS));
            // koniec testu


            //Skaner do testow
            System.out.println(" ");
            System.out.println("Sprawdz inne lancuchy s");
            System.out.println("Podaj lancuch s w ktorym znajdziemy najdluzszy powtorzony prefiks, " +
                    "wynik pusty oznacza brak powtarzajacego się prefiksu: ");

            Scanner lancuch_s = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
            String s1 = lancuch_s.nextLine()+"$";


            saveAlphabet(s1);

            Node root1 = buildSuffixTree(s1);

            //Jesli chcemy wydrukowac drzewo nalezy odkomentowac
            //printTree(root1, s1, 0);

            CharSequence depthLLS1 = depthLeafLongestSuffix(root1);
            System.out.println(" ");
            System.out.println("Najdluzszy powtorzony prefiks dla tekstu s = " + s1 + " to: ");
            System.out.println(" ");

            System.out.println(Search_depthLeafLongestSuffix(s1, depthLLS1));


        }
        catch(StringIndexOutOfBoundsException err){
            System.out.println("USTAW ALPHABET "+err);
        }


    }

}

