package com.example.guru.util;

import com.example.guru.model.entity.enums.InterviewType;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@UtilityClass
public class CallBackDataHelper {

    private final String DELIMITER = "|";

    /**
     * The method converts callBackQueryData into 3 parameters, which are used to determine the next steps of the interview(game).
     * This is also a rule for forming callBackQueryData.
     * String[0] - An InterviewSession identifier. -1 if that`s the first request.
     * String[1] - InterviewType
     * String[2] - answer`s id on the last question. -1 in case of finishing the interview
     */
    public String[] parseCallBack(CallbackQuery query) {
        String[] result = new String[3];
        String data = query.getData();
        int firstDel = data.indexOf(DELIMITER);
        int secondDel = data.indexOf(DELIMITER, firstDel + 1);

        result[0] = data.substring(0, firstDel);
        result[1] = data.substring(firstDel + 1, secondDel);
        result[2] = data.substring(secondDel + 1);

        return result;
    }

    /**
     * The method create callBackQueryData.
     *
     * @param interviewSessionId unique identification of the interview session. -1 in case of the first request.
     * @param interviewType      type of interview.(Quiz, Competitive, Interactive/AI)
     * @param answer             unique identification of user`s answer. -1 in case of finishing the interview.
     * @return callBackMessage
     */
    public String buildCallBackMsg(Long interviewSessionId, InterviewType interviewType, String answer) {
        return interviewSessionId + DELIMITER + interviewType.name() + DELIMITER + answer;
    }
}
