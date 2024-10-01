package com.dangun.miniproject.fixture;

import com.dangun.miniproject.common.code.Code;
import com.dangun.miniproject.common.code.CodeKey;

public class CodeFixture {

    public static Code instanceOf(CodeKey codeKey){
        return new Code(codeKey, "판매중", "sale", 1);
    }
}
