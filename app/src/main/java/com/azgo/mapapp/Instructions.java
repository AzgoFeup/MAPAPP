package com.azgo.mapapp;

import java.util.LinkedList;
import java.util.regex.Pattern;

import android.util.Log;
/**
 * Created by paulo on 09/01/2017.
 */

public class Instructions {

    public class Instruction {
        public String text;
        public Graph.Node node;

        public Instruction(String text, Graph.Node node) {
            this.text = text;
            this.node = node;
        }
        public String getText() {return text;}
        public void setText(String text) {this.text = text;}

    }

    public static Instruction[] calculateInstructions (LinkedList<Graph.Node> allNodes) {
        //Create return variable with instructions and node
        Instruction[] instructions = new Instruction[999];

        //Variaveis auxiliares
       /* Graph.Node dirAtual = null;
        Graph.Node dirAnt = null;*/
        //Graph.Node pontoAtual = null;
        //Graph.Node pontoAnt = null;
        double pontoAtual_lat = 0;
        double pontoAtual_lng = 0;
        int pontoAtual_index = 0;
        double pontoAnt_lat = 0;
        double pontoAnt_lng = 0;
        int pontoAnt_index = 0;

        int pontoAntAnt_index = 0;
        double pontoAntAnt_lat = 0 ;
        double pontoAntAnt_lng = 0 ;

        int dirAtual_lat = -10;
        int dirAtual_lng = -10;
        int dirAnt_lat = -10;
        int dirAnt_lng = -10;
        String instrucao = null;
        String instrucaoWait = null;
        //estas flags vao indicar o tipo de ponto
        // 0-> sala
        // 1-> corredor 0
        // 4 -> corredor 1
        // 5 -> corredor 2
        // 6 -> corredor 3
        // 2 -> escadas/elevador
        // 3 -> saída

        int flagAtual = -1;
        int flagAnt = -1;
        boolean flag = false;
        int wait = 0;

        double latitude = 0;
        double longitude = 0;
        int index_inst = 0;
        //Instruction auxiliar;
        int npontos = allNodes.size();
        int index_pontos = 0;

        for(Graph.Node no : allNodes) {

            pontoAnt_lat = pontoAtual_lat;
            pontoAnt_lng = pontoAtual_lng;
            pontoAnt_index = pontoAtual_index;

            pontoAtual_lat = no.getLatitude();
            pontoAtual_lng = no.getLongitude();
            pontoAtual_index = no.getIndex();

            //pontoAtual.equals(no);
            dirAnt_lat = dirAtual_lat;
            dirAnt_lng = dirAtual_lng;
            //dirAnt.equals(dirAtual);
            instrucao = null;

            flagAnt = flagAtual;

            //Verificar se tem dois pontos, para ver a direcção que tem de tomar
            if((pontoAtual_lat + pontoAtual_lng)!= 0 && (pontoAnt_lat + pontoAnt_lng)!= 0) {
                latitude =pontoAtual_lat - pontoAnt_lat;
                longitude = pontoAtual_lng - pontoAnt_lng;
                //Log.e("Ant e Atual", pontoAnt_index + " " + pontoAtual_index);
                //Log.e("ATUAL", "ANT" );
                if(Math.abs(latitude)>Math.abs(longitude)){
                    dirAtual_lng = 0;
                    if (latitude>0) {
                        dirAtual_lat = 1;
                    } else {
                        dirAtual_lat = -1;
                    }
                } else {
                    dirAtual_lat = 0;
                    if(longitude>0){
                        dirAtual_lng = 1;
                    } else {
                        dirAtual_lng = -1;
                    }
                }
                Log.e("DIR", dirAtual_lat + " " + dirAtual_lng);


            }
            // Verificar se os pontos são salas ou corredores
            Pattern b = Pattern.compile("B*");
            Pattern exit = Pattern.compile("Exit*");

            if(no.getLabel().equals("corredor") == true) {
                //corredor piso 0
                flagAtual = 1;
            } else if(no.getLabel().equals("corredor1") == true) {
                //corredor piso 1
                flagAtual = 4;
            } else if(no.getLabel().equals("corredor2") == true) {
                //corredor piso 2
                flagAtual = 5;
            } else if(no.getLabel().equals("corredor3") == true) {
                //corredor piso 3
                flagAtual = 6;
            } else if(no.getLabel().equals("escadas") == true) {
                //corredor piso 3
                flagAtual = 2;
            } else if (exit.matcher(no.getLabel()).find() == true) {
                //exit
                flagAtual = 3;
            } else if (b.matcher(no.getLabel()).find() == true) {
                //sala
                flagAtual = 0;
            }

            if(flagAnt ==0 && flagAtual==1) {
                instrucao = "Go out of the room through the corridor";
                flag = true;
            }
            else if (flagAnt == 1 && flagAtual == 0) {
                instrucao = "Get in the room";
                flag = true;
            }
            else if (flagAnt == 1 && flagAtual == 3) {
                instrucao = "Follow the exit";
                flag = true;
            }
            else if (flagAnt == 3 && flagAtual == 1) {
                instrucao = "Get in the building";
                flag = true;
            }
            else if ((flagAnt == 1 && flagAtual==1) || (flagAnt == 4 && flagAtual==4) || (flagAnt == 5 && flagAtual==5)  || (flagAnt == 6 && flagAtual==6) ) {
                instrucao ="Follow the corridor";
                flag = true;
            }
            else if ((flagAnt==1 || flagAnt==4 || flagAnt==5 || flagAnt==6) && (flagAtual==2)) {
                instrucaoWait = "Follow the stair or the elevator";
                pontoAntAnt_index = pontoAnt_index;
                pontoAntAnt_lat = pontoAnt_lat;
                pontoAntAnt_lng = pontoAnt_lng;
                flag = true;
                wait = 1;
            }
            else if ( flagAnt == 2 && flagAtual == 1) {
                instrucaoWait = instrucaoWait + " to the floor 0";
                wait = 2;
            }
            else if ( flagAnt == 2 && flagAtual == 4) {
                instrucaoWait = instrucaoWait + " to the 1st floor ";
                wait = 2;
            }
            else if ( flagAnt == 2 && flagAtual == 5) {
                instrucaoWait = instrucaoWait + " to the 2nd floor ";
                wait = 2;
            }
            else if ( flagAnt == 2 && flagAtual == 6) {
                instrucaoWait = instrucaoWait + " to the 3rd floor ";
                wait = 2;
            }
            Log.e("FLAG anterior",pontoAnt_index + " " + flagAnt);
            Log.e("FLAG atual",pontoAtual_index + " " + flagAtual);
            //Verificar se há mudança de direcção
            if(dirAtual_lat!= -10 && dirAtual_lng!= -10 && dirAnt_lat!= -10 && dirAnt_lng!=-10) {
                //Seguir em frente
                if(dirAtual_lat == dirAnt_lat && dirAtual_lng == dirAnt_lng) {
                    if (flag == true) {
                        instrucao = instrucao + " forward";
                        flag = false;
                    } else {
                        instrucao = "Go forward";
                    }
                    if (flagAnt==1 && flagAtual==1) {
                        if (dirAnt_lat == -1 || dirAnt_lng==0)
                            instrucao = instrucao +" with the main window on your back";
                        else if (dirAnt_lat == 1 || dirAnt_lng==0)
                            instrucao = instrucao +" with the main window on your front";
                        else if (dirAnt_lat == 0 || dirAnt_lng== -1)
                            instrucao = instrucao +" with the main window on your right";
                        else if (dirAnt_lat == 0 || dirAnt_lng== 1)
                            instrucao = instrucao +" with the main window on your left";
                    }
                }
                //Virar á direita
                if((dirAtual_lat == 0 && dirAtual_lng == 1 && dirAnt_lat == 1  && dirAnt_lng == 0) || (dirAtual_lat == 0 && dirAtual_lng == -1 && dirAnt_lat == -1  && dirAnt_lng == 0) || (dirAtual_lat == -1 && dirAtual_lng == 0 && dirAnt_lat == 0  && dirAnt_lng == 1)|| (dirAtual_lat == 1 && dirAtual_lng == 0 && dirAnt_lat == 0  && dirAnt_lng == -1)) {
                    if (flag == true){
                        instrucao=instrucao+" right";
                        flag = false;
                    } else {
                        instrucao = "Turn right";
                    }
                }
                if((dirAtual_lat == 0 && dirAtual_lng == -1 && dirAnt_lat == 1  && dirAnt_lng == 0) || (dirAtual_lat == 0 && dirAtual_lng == 1 && dirAnt_lat == -1  && dirAnt_lng == 0) || (dirAtual_lat == 1 && dirAtual_lng == 0 && dirAnt_lat == 0  && dirAnt_lng == 1)|| (dirAtual_lat == -1 && dirAtual_lng == 0 && dirAnt_lat == 0  && dirAnt_lng == -1)) {
                    if(flag == true) {
                        instrucao = instrucao + " left";
                        flag = false;
                    } else {
                        instrucao = "Turn left";
                    }

                }
                if((dirAtual_lat == -1 && dirAtual_lng == 0 && dirAnt_lat == 1  && dirAnt_lng == 0) || (dirAtual_lat == 1 && dirAtual_lng == 0 && dirAnt_lat == -1  && dirAnt_lng == 0) || (dirAtual_lat == 0 && dirAtual_lng == -1 && dirAnt_lat == 0  && dirAnt_lng == 1)|| (dirAtual_lat == 0 && dirAtual_lng == 1 && dirAnt_lat == 0  && dirAnt_lng == -1)) {
                    if (flag == true){
                        instrucao = instrucao + " back";
                        flag = false;
                    } else {
                        instrucao = "Go back";
                    }
                }

            }

            //Adicionar os pontos e as instruçoes a lista
            if (instrucao != null) {
                if (wait == 1) {

                } else if (wait == 2) {
                    Graph.Node aux = new Graph().new Node(pontoAntAnt_index, "nada", pontoAntAnt_lat, pontoAntAnt_lng);
                    instructions[index_inst] = new Instructions().new Instruction(instrucaoWait, aux);
                    index_inst++;
                    wait = 0;
                } else {
                    //Graph.Node aux = new Graph.Node(0,null,pontoAnt_lat,pontoAnt_lng);
                    //Log.e("Adicionar",instrucao + " " + pontoAnt_index);
                    Graph.Node aux = new Graph().new Node(pontoAnt_index, "nada", pontoAnt_lat, pontoAnt_lng);
                    instructions[index_inst] = new Instructions().new Instruction(instrucao, aux);
                    index_inst++;
                }
                //Log.e("ADICIONEI",auxiliar.getText() + " " + auxiliar.node.getIndex());

            }
            index_pontos++;
        }
        instructions[index_inst] = new Instructions().new Instruction("You have arrived to your destination", allNodes.getLast());

        return instructions;
    }
}
