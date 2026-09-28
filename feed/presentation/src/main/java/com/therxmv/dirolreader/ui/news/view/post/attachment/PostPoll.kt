package com.therxmv.dirolreader.ui.news.view.post.attachment

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.therxmv.dirolreader.domain.models.PollModel
import com.therxmv.dirolreader.domain.models.PollOptionModel
import com.therxmv.dirolreader.feed.presentation.R
import kotlinx.coroutines.launch

private const val PERCENT_DIVISOR = 100f
private const val CHOSEN_FILL_ALPHA = 0.35f
private const val OPTION_FILL_ALPHA = 0.15f
private const val VOTING_ALPHA = 0.6f

/**
 * Poll card: question, meta line and answer options with vote
 * counts/percentages. Open polls accept votes via [onVote]; closed polls and
 * already-answered quizzes render read-only results.
 */
@Composable
fun PostPoll(
    poll: PollModel,
    onVote: suspend (IntArray) -> PollModel?,
) {
    // Keyed on the whole model: a refresh with the same poll id but new data
    // (other votes, closed flag) must replace the rendered state.
    var currentPoll by remember(poll) { mutableStateOf(poll) }
    var selectedOptions by remember(poll.id) { mutableStateOf(emptySet<Int>()) }
    var isVoting by remember(poll.id) { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val voteFailedMessage = stringResource(id = R.string.feed_poll_vote_failed)

    val submitVote: (IntArray) -> Unit = { optionIds ->
        coroutineScope.launch {
            isVoting = true
            val updatedPoll = onVote(optionIds)
            if (updatedPoll != null) {
                currentPoll = updatedPoll
                selectedOptions = emptySet()
            } else {
                Toast.makeText(context, voteFailedMessage, Toast.LENGTH_SHORT).show()
            }
            isVoting = false
        }
    }

    val hasVoted = currentPoll.hasVoted
    val uiState = PollUiState(
        poll = currentPoll,
        selectedOptions = selectedOptions,
        canVote = !currentPoll.isClosed && !isVoting && !(currentPoll.isQuiz && hasVoted),
        showResults = currentPoll.isClosed || hasVoted,
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        PollHeader(poll = currentPoll)
        Spacer(modifier = Modifier.height(8.dp))
        PollOptionsSection(
            modifier = Modifier.alpha(if (isVoting) VOTING_ALPHA else 1f),
            uiState = uiState,
            onOptionClick = { index ->
                if (currentPoll.allowMultipleAnswers) {
                    selectedOptions = if (index in selectedOptions) {
                        selectedOptions - index
                    } else {
                        selectedOptions + index
                    }
                } else {
                    submitVote(intArrayOf(index))
                }
            },
            onSubmit = { submitVote(selectedOptions.toIntArray()) },
        )
    }
}

private data class PollUiState(
    val poll: PollModel,
    val selectedOptions: Set<Int>,
    val canVote: Boolean,
    val showResults: Boolean,
)

@Composable
private fun PollOptionsSection(
    uiState: PollUiState,
    onOptionClick: (Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = uiState.poll.options

    Column(modifier = modifier) {
        options.forEachIndexed { index, option ->
            PollOptionRow(
                option = option,
                optionState = PollOptionState(
                    isSelected = index in uiState.selectedOptions,
                    isCorrect = uiState.showResults && index == uiState.poll.correctOptionId,
                    showResults = uiState.showResults,
                ),
                enabled = uiState.canVote,
                onClick = { onOptionClick(index) },
            )
            if (index != options.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        if (uiState.poll.allowMultipleAnswers && uiState.canVote && uiState.selectedOptions.isNotEmpty()) {
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onSubmit,
            ) {
                Text(text = stringResource(id = R.string.feed_poll_vote_button))
            }
        }
    }
}

@Composable
private fun PollHeader(poll: PollModel) {
    Text(
        text = poll.question,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
    )
    PollMeta(poll = poll)
}

@Composable
private fun PollMeta(poll: PollModel) {
    val meta = listOfNotNull(
        stringResource(id = if (poll.isQuiz) R.string.feed_poll_quiz else R.string.feed_poll),
        stringResource(id = R.string.feed_poll_anonymous).takeIf { poll.isAnonymous },
        LocalContext.current.resources.getQuantityString(
            R.plurals.feed_poll_votes,
            poll.totalVoterCount,
            poll.totalVoterCount,
        ),
        stringResource(id = R.string.feed_poll_closed).takeIf { poll.isClosed },
    ).joinToString(" · ")

    Text(
        text = meta,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

private data class PollOptionState(
    val isSelected: Boolean,
    val isCorrect: Boolean,
    val showResults: Boolean,
)

@Composable
private fun PollOptionRow(
    option: PollOptionModel,
    optionState: PollOptionState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val isMarked = option.isChosen || optionState.isSelected || optionState.isCorrect
    val stateDescription = stringResource(
        id = if (isMarked) R.string.feed_poll_option_chosen else R.string.feed_poll_option_not_chosen,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { this.stateDescription = stateDescription },
    ) {
        if (optionState.showResults && option.votePercentage > 0) {
            OptionResultFill(
                fraction = option.votePercentage / PERCENT_DIVISOR,
                isHighlighted = option.isChosen || optionState.isCorrect,
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(
                    id = if (isMarked) R.drawable.poll_selected_icon else R.drawable.poll_unselected_icon,
                ),
                contentDescription = optionContentDescription(
                    isMarked = isMarked,
                    isCorrect = optionState.isCorrect,
                ),
                tint = if (isMarked) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.weight(1f),
                text = option.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (optionState.showResults) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${option.votePercentage}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun optionContentDescription(isMarked: Boolean, isCorrect: Boolean) = when {
    isCorrect -> stringResource(id = R.string.feed_poll_correct_answer)
    isMarked -> stringResource(id = R.string.feed_poll_option_chosen)
    else -> null
}

@Composable
private fun BoxScope.OptionResultFill(
    fraction: Float,
    isHighlighted: Boolean,
) {
    val fillColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primary.copy(alpha = CHOSEN_FILL_ALPHA)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = OPTION_FILL_ALPHA)
    }

    Box(
        modifier = Modifier
            .matchParentSize()
            .clip(MaterialTheme.shapes.small),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(fillColor),
        )
    }
}

@Preview
@Composable
private fun PostPollPreview() {
    PostPoll(
        poll = PollModel(
            id = 1L,
            question = "Which feature should ship next?",
            options = listOf(
                PollOptionModel(text = "Polls", voterCount = 6, votePercentage = 60, isChosen = true),
                PollOptionModel(text = "Threads", voterCount = 3, votePercentage = 30, isChosen = false),
                PollOptionModel(text = "Stories", voterCount = 1, votePercentage = 10, isChosen = false),
            ),
            totalVoterCount = 10,
            isAnonymous = true,
            isClosed = false,
            isQuiz = false,
            allowMultipleAnswers = false,
            correctOptionId = -1,
        ),
        onVote = { null },
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PostPollClosedPreview() {
    PostPoll(
        poll = PollModel(
            id = 2L,
            question = "Was this useful?",
            options = listOf(
                PollOptionModel(text = "Yes", voterCount = 8, votePercentage = 80, isChosen = false),
                PollOptionModel(text = "No", voterCount = 2, votePercentage = 20, isChosen = false),
            ),
            totalVoterCount = 10,
            isAnonymous = false,
            isClosed = true,
            isQuiz = false,
            allowMultipleAnswers = false,
            correctOptionId = -1,
        ),
        onVote = { null },
    )
}
