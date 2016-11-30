package com.azgo.mapapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulo on 25/11/2016.
 */

public class Graph {
    private List<Node> nodes;
    public int V = 69;
    private boolean[][] adj= new boolean[69][69];

    public class Node{
        private int index;
        private String label;
        private double latitude;
        private double longitude;

        public Node(int index, String label, double latitude, double longitude){
            this.index = index;
            this.label = label;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public int getIndex(){
            return index;
        }
        public String getLabel(){return label;}
        public double getLatitude(){
            return latitude;
        }
        public double getLongitude(){
            return longitude;
        }

    }

    public List<Node> getListNodes(){
        return nodes;
    }

    //make nodes and insert values
    public List<Node> insertNodes() {
        nodes = new ArrayList<>();
        Node node0 = new Node(0,"B005", 41.177258,-8.595166); Node node1 = new Node(1, "B004", 41.177362, -8.595123);
        Node node2 = new Node(2, "B006", 41.177252, -8.595294); Node node3 = new Node(3, "B007", 41.177370, -8.595250);
        Node node4 = new Node(4, "B008", 41.177468, -8.595274); Node node5 = new Node(5, "B009", 41.177514, -8.595428);
        Node node6 = new Node(6, "B010", 41.177426, -8.595551); Node node7 = new Node(7, "B011", 41.177348, -8.595575);
        Node node8 = new Node(8, "B012", 41.177376, -8.595702); Node node9 = new Node(9, "B013", 41.177477, -8.595661);
        Node node10 = new Node(10, "B037", 41.177619, -8.595833); Node node11 = new Node(11, "B001", 41.177823, -8.595744);
        Node node12 = new Node(12, "B002", 41.177790, -8.595638); Node node13 = new Node(13, "B003", 41.177717, -8.595551);
        Node node14 = new Node(14, "B015", 41.177575, -8.595916); Node node15 = new Node(15, "B014", 41.177475, -8.595963);
        Node node16 = new Node(16, "B016", 41.177465, -8.596091); Node node17 = new Node(17, "B017", 41.177563, -8.596046);
        Node node18 = new Node(18, "B018", 41.177677, -8.596112); Node node19 = new Node(19, "B019", 41.177687, -8.596240);
        Node node20 = new Node(20, "B020", 41.177601, -8.596348); Node node21 = new Node(21, "B021", 41.177519, -8.596386);
        Node node22 = new Node(22, "B022", 41.177532, -8.596497); Node node23 = new Node(23, "B023", 41.177652, -8.596476);
        Node node24 = new Node(24, "B024", 41.177745, -8.596520); Node node25 = new Node(25, "B025", 41.177772, -8.596670);
        Node node26 = new Node(26, "B027", 41.177587, -8.596797); Node node27 = new Node(27, "B026", 41.177686, -8.596774);
        Node node28 = new Node(28, "B028", 41.177597, -8.596927); Node node29 = new Node(29, "B029", 41.177720, -8.596882);
        Node node30 = new Node(30, "B030", 41.177812, -8.596949); Node node31 = new Node(31, "B031", 41.177840, -8.597077);
        Node node32 = new Node(32, "B032", 41.177754, -8.597185); Node node33 = new Node(33, "B033", 41.177675, -8.597205);
        Node node34 = new Node(34, "B034", 41.177684, -8.597335); Node node35 = new Node(35, "B035", 41.177781, -8.597312);
        Node node36 = new Node(36, "Exit A", 41.177927, -8.597303); Node node37 = new Node(37, "Exit B-1", 41.177873, -8.596771);
        Node node38 = new Node(38, "Exit B-2", 41.177704, -8.595821); Node node39 = new Node(39, "Exit B-3", 41.177471, -8.595087);
        Node node40 = new Node(40, "corredor", 41.177254, -8.595235); Node node41 = new Node(41, "corredor", 41.177356, -8.595187);
        Node node42 = new Node(42, "corredor", 41.177420, -8.595159); Node node43 = new Node(43, "corredor", 41.177483, -8.595146);
        Node node44 = new Node(44, "corredor", 41.177531, -8.595320); Node node45 = new Node(45, "corredor", 41.177582, -8.595515);
        Node node46 = new Node(46, "corredor", 41.177538, -8.595545); Node node47 = new Node(47, "corredor", 41.177455, -8.595590);
        Node node48 = new Node(48, "corredor", 41.177356, -8.595641); Node node49 = new Node(49, "corredor", 41.177655, -8.595737);
        Node node50 = new Node(50, "corredor", 41.177696, -8.595960); Node node51 = new Node(51, "corredor", 41.177649, -8.595974);
        Node node52 = new Node(52, "corredor", 41.177572, -8.595991); Node node53 = new Node(53, "corredor", 41.177465, -8.596023);
        Node node54 = new Node(54, "corredor", 41.177724, -8.596161); Node node55 = new Node(55, "corredor", 41.177763, -8.596360);
        Node node56 = new Node(56, "corredor", 41.177715, -8.596380); Node node57 = new Node(57, "corredor", 41.177620, -8.596414);
        Node node58 = new Node(58, "corredor", 41.177526, -8.596443); Node node59 = new Node(59, "corredor", 41.177804, -8.596575);
        Node node60 = new Node(60, "corredor", 41.177840, -8.596794); Node node61 = new Node(61, "corredor", 41.177789, -8.596803);
        Node node62 = new Node(62, "corredor", 41.177696, -8.596831); Node node63 = new Node(63, "corredor", 41.177601, -8.596860);
        Node node64 = new Node(64, "corredor", 41.177878, -8.597000); Node node65 = new Node(65, "corredor", 41.177911, -8.597194);
        Node node66 = new Node(66, "corredor", 41.177864, -8.597216); Node node67 = new Node(67, "corredor", 41.177769, -8.597247);
        Node node68 = new Node(68, "corredor", 41.177671, -8.597276);
        nodes.add(node0); nodes.add(node1); nodes.add(node2); nodes.add(node3); nodes.add(node4);
        nodes.add(node5); nodes.add(node6); nodes.add(node7); nodes.add(node8); nodes.add(node9);
        nodes.add(node10); nodes.add(node11); nodes.add(node12); nodes.add(node13); nodes.add(node14);
        nodes.add(node15); nodes.add(node16); nodes.add(node17); nodes.add(node18); nodes.add(node19);
        nodes.add(node20); nodes.add(node21); nodes.add(node22); nodes.add(node23); nodes.add(node24);
        nodes.add(node25); nodes.add(node26); nodes.add(node27); nodes.add(node28); nodes.add(node29);
        nodes.add(node30); nodes.add(node31); nodes.add(node32); nodes.add(node33); nodes.add(node34);
        nodes.add(node35); nodes.add(node36); nodes.add(node37); nodes.add(node38); nodes.add(node39);
        nodes.add(node40); nodes.add(node41); nodes.add(node42); nodes.add(node43); nodes.add(node44);
        nodes.add(node45); nodes.add(node46); nodes.add(node47); nodes.add(node48); nodes.add(node49);
        nodes.add(node50); nodes.add(node51); nodes.add(node52); nodes.add(node53); nodes.add(node54);
        nodes.add(node55); nodes.add(node56); nodes.add(node57); nodes.add(node58); nodes.add(node59);
        nodes.add(node60); nodes.add(node61); nodes.add(node62); nodes.add(node63); nodes.add(node64);
        nodes.add(node65); nodes.add(node66); nodes.add(node67); nodes.add(node68);

        return nodes;
    }
    public Node getNode(int index){
        return nodes.get(index);
    }

    public boolean [][] fillMatrix() {
        //preencher a matriz de adjacências
        //entries in boolean arrays are default to false


        adj[1-1][41-1] = true; adj[2-1][42-1] = true; adj[3-1][41-1] = true; adj[4-1][42-1] = true;
        adj[5-1][45-1] = true; adj[5-1][6-1] = true; adj[6-1][45-1] = true; adj[6-1][5-1] = true;
        adj[7-1][48-1] = true; adj[8-1][49-1] = true; adj[9-1][49-1] = true; adj[10-1][48-1] = true;
        adj[11-1][50-1] = true; adj[12-1][50-1] = true; adj[13-1][50-1] = true; adj[14-1][50-1] = true;
        adj[15-1][53-1] = true; adj[16-1][54-1] = true; adj[17-1][54-1] = true; adj[18-1][53-1] = true;
        adj[19-1][55-1] = true; adj[19-1][20-1] = true; adj[20-1][55-1] = true; adj[20-1][19-1] = true;
        adj[21-1][58-1] = true; adj[22-1][59-1] = true; adj[23-1][59-1] = true; adj[24-1][58-1] = true;
        adj[25-1][60-1] = true; adj[25-1][26-1] = true; adj[26-1][60-1] = true; adj[26-1][25-1] = true;
        adj[27-1][64-1] = true; adj[28-1][63-1] = true; adj[29-1][64-1] = true; adj[30-1][63-1] = true;
        adj[31-1][65-1] = true; adj[31-1][32-1] = true; adj[32-1][65-1] = true; adj[32-1][31-1] = true;
        adj[33-1][68-1] = true; adj[34-1][69-1] = true; adj[35-1][69-1] = true; adj[36-1][68-1] = true;
        adj[37-1][66-1] = true; adj[38-1][61-1] = true; adj[39-1][50-1] = true; adj[40-1][44-1] = true;
        adj[41-1][1-1] = true; adj[41-1][3-1] = true; adj[41-1][42-1] = true; adj[42-1][2-1] = true;
        adj[42-1][4-1] = true; adj[42-1][43-1] = true; adj[42-1][41-1] = true; adj[43-1][42-1] = true;
        adj[43-1][44-1] = true; adj[44-1][43-1] = true; adj[44-1][45-1] = true; adj[45-1][44-1] = true;
        adj[45-1][5-1] = true; adj[45-1][6-1] = true; adj[45-1][46-1] = true; adj[46-1][45-1] = true;
        adj[46-1][47-1] = true; adj[46-1][50-1] = true; adj[47-1][46-1] = true; adj[47-1][48-1] = true;
        adj[48-1][47-1] = true; adj[48-1][7-1] = true; adj[48-1][10-1] = true; adj[48-1][49-1] = true;
        adj[49-1][48-1] = true; adj[49-1][8-1] = true; adj[49-1][9-1] = true; adj[50-1][46-1] = true;
        adj[50-1][11-1] = true; adj[50-1][39-1] = true; adj[50-1][12-1] = true; adj[50-1][13-1] = true;
        adj[50-1][14-1] = true; adj[51-1][50-1] = true; adj[51-1][52-1] = true; adj[51-1][55-1] = true;
        adj[52-1][51-1] = true; adj[52-1][53-1] = true; adj[53-1][15-1] = true; adj[56-1][60-1] = true;
        adj[53-1][18-1] = true; adj[53-1][52-1] = true; adj[53-1][54-1] = true; adj[54-1][53-1] = true;
        adj[54-1][17-1] = true; adj[54-1][16-1] = true; adj[55-1][56-1] = true; adj[55-1][20-1] = true;
        adj[55-1][19-1] = true; adj[55-1][51-1] = true; adj[56-1][55-1] = true; adj[56-1][57-1] = true;
        adj[57-1][56-1] = true; adj[57-1][58-1] = true; adj[58-1][57-1] = true; adj[58-1][21-1] = true;
        adj[58-1][24-1] = true; adj[58-1][59-1] = true; adj[59-1][58-1] = true; adj[59-1][22-1] = true;
        adj[59-1][23-1] = true; adj[60-1][56-1] = true; adj[60-1][25-1] = true; adj[60-1][26-1] = true;
        adj[60-1][61-1] = true; adj[61-1][60-1] = true; adj[61-1][38-1] = true; adj[61-1][62-1] = true;
        adj[61-1][65-1] = true; adj[62-1][61-1] = true; adj[62-1][63-1] = true; adj[63-1][62-1] = true;
        adj[63-1][64-1] = true; adj[63-1][30-1] = true; adj[63-1][28-1] = true; adj[64-1][63-1] = true;
        adj[64-1][27-1] = true; adj[64-1][29-1] = true; adj[65-1][61-1] = true; adj[65-1][31-1] = true;
        adj[65-1][32-1] = true; adj[65-1][66-1] = true; adj[66-1][37-1] = true; adj[66-1][67-1] = true;
        adj[66-1][65-1] = true; adj[67-1][66-1] = true; adj[67-1][68-1] = true; adj[68-1][67-1] = true;
        adj[68-1][69-1] = true; adj[68-1][33-1] = true; adj[68-1][36-1] = true; adj[69-1][35-1] = true;
        adj[69-1][34-1] = true; adj[69-1][68-1] = true; adj[44-1][40-1] = true; adj[50-1][51-1] = true;
        adj[56-1][60-1] = true;

        return adj;
    }

    public boolean privateGet(int x, int y){
        return adj[x][y];
    }

}