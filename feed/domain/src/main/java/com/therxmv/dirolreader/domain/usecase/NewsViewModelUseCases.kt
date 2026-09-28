package com.therxmv.dirolreader.domain.usecase

import com.therxmv.dirolreader.domain.usecase.channel.UpdateChannelRatingUseCase
import com.therxmv.dirolreader.domain.usecase.message.DownloadMediaAndGetPathUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetMessagePagingUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetReadablePostTimeUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetSavedMessagesUseCase
import com.therxmv.dirolreader.domain.usecase.message.GetUnreadChannelsFlowUseCase
import com.therxmv.dirolreader.domain.usecase.message.MarkAllAsReadUseCase
import com.therxmv.dirolreader.domain.usecase.message.MarkMessageAsReadUseCase
import com.therxmv.dirolreader.domain.usecase.message.SaveMessageUseCase
import com.therxmv.dirolreader.domain.usecase.message.SearchMessagesUseCase
import com.therxmv.dirolreader.domain.usecase.message.TranslateTextUseCase
import com.therxmv.dirolreader.domain.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

data class NewsViewModelUseCases @Inject constructor(
    val getCurrentUser: GetCurrentUserUseCase,
    val getNewsPaging: GetMessagePagingUseCase,
    val updateChannelRating: UpdateChannelRatingUseCase,
    val downloadMediaAndGetPath: DownloadMediaAndGetPathUseCase,
    val markMessageAsRead: MarkMessageAsReadUseCase,
    val getReadablePostTime: GetReadablePostTimeUseCase,
    val getUnreadChannelsFlow: GetUnreadChannelsFlowUseCase,
    val saveMessage: SaveMessageUseCase,
    val getSavedMessages: GetSavedMessagesUseCase,
    val markAllAsRead: MarkAllAsReadUseCase,
    val translateText: TranslateTextUseCase,
    val searchMessages: SearchMessagesUseCase,
)
