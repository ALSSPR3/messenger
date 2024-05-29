package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

import lombok.Data;

@Data
public class Vote {

	private String voteResult;

	public String Vote(int roomUser) throws IOException {

		int N = roomUser;

		StringTokenizer st = new StringTokenizer(null);

		int A = 0;
		int R = 0;
		int I = 0;

		for (int i = 0; i < N; i++) {
			int result = Integer.parseInt(st.nextToken());

			if (result == 1) { // 찬성표
				A++;
			} else if (result == -1) { // 반대표
				R++;
			} else {
				I++;
			}
		}

		int M = 0;
		if (N % 2 != 0) {
			M = (N / 2) + 1;
		} else {
			M = N / 2;
		}

		if (I >= M) {
			return voteResult = ("INVALID");

		}

		if (A > R) {
			return voteResult = ("APPROVED");
		} else {
			return voteResult = ("REJECTED");
		}
	}

}
