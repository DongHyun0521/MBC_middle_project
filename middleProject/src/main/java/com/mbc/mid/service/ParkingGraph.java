package com.mbc.mid.service;

import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class ParkingGraph {

    private final Map<Integer, List<Integer>> graph = new HashMap<>();

    public ParkingGraph(){
        buildGraph();
    }

    // =========================
    // 그래프 생성
    // =========================
    public void buildGraph(){

        graph.clear();

        // =========================
        // 통로 노드 (ENTRY + 세로통로)
        // =========================
        for(int i=1;i<=10;i++){
            if(i<10) connect(i, i+1);
        }

        // =========================
        // 주차칸 자동 연결
        // =========================
        // 규칙:
        // spotId 1~10 -> node 5001~5010
        // 통로 6번에 연결 (중앙 진입 통로)
        for(int spotId=1; spotId<=200; spotId++){   // 최대 200칸 가정
            int node = 5000 + spotId;
            connect(6, node);
        }
    }

    private void connect(int a, int b){
        graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
        graph.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
    }

    public Map<Integer, List<Integer>> getGraph(){
        return graph;
    }
}
