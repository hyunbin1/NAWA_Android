package com.example.myapplication.data.DTO.Response

import com.example.myapplication.data.model.Member

data class MembershipResponse(
    val data: List<JoinedMemberDTO>,
    val total: Int,
    val offset: Int,
    val limit: Int
) {
    // 추가적으로 유저가 클럽에 가입되어 있는지 확인하는 메서드
    fun isMember(email: String): Boolean {
        return data?.any { it.emailID == email } ?: false
    }
}

data class JoinedMemberDTO(
    val emailID: String,
    val memberName: String,
    val profileImage: String
)
