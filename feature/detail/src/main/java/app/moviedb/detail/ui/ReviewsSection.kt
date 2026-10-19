package app.moviedb.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.moviedb.core.common.state.SectionState
import app.moviedb.core.model.Review
import app.moviedb.designsystem.component.ErrorCard
import app.moviedb.designsystem.component.RemoteImage
import app.moviedb.designsystem.format.toCardDate
import app.moviedb.designsystem.format.toRatingLabel
import app.moviedb.designsystem.theme.MoviedbTheme
import app.moviedb.detail.R

private const val COLLAPSED_LINES = 3

/** The "Reviews" heading, a count pill, and the list in whatever state it is in. */
@Composable
internal fun ReviewsSection(
    state: SectionState<List<Review>>,
    expandedIds: Set<String>,
    onToggleExpanded: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        ReviewsHeader(count = (state as? SectionState.Content)?.data?.size)

        when (state) {
            SectionState.Loading -> ReviewsSkeleton()
            SectionState.Empty -> NoReviews()
            is SectionState.Error -> ErrorCard(
                title = stringResource(R.string.detail_error_reviews_title),
                message = stringResource(state.error.messageRes()),
                onRetry = onRetry,
            )
            is SectionState.Content -> Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                state.data.forEach { review ->
                    ReviewCard(
                        review = review,
                        expanded = review.id in expandedIds,
                        onToggleExpanded = { onToggleExpanded(review.id) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ReviewsHeader(count: Int?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 32.dp, bottom = 12.dp),
    ) {
        Text(
            text = stringResource(R.string.detail_reviews),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.semantics { heading() },
        )
        if (count != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .defaultMinSize(minWidth = 32.dp, minHeight = 32.dp)
                    .clip(MaterialShapes.Cookie9Sided.toShape())
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 6.dp),
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

/**
 * One review. The card reads as a single unit. "Read more" appears only when the text is
 * really cut off, which is measured from the text layout, not guessed from its length.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun ReviewCard(
    review: Review,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var overflowing by remember(review.id) { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.semantics(mergeDescendants = true) {},
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape())
                        .background(MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Text(
                        text = review.authorName.firstOrNull()?.uppercase().orEmpty(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    RemoteImage(url = review.avatarUrl, modifier = Modifier.fillMaxSize())
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = review.authorName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    val date = review.createdAt.toCardDate()
                    if (date.isNotEmpty()) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                ReviewRating(review.rating)
            }

            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = if (expanded) Int.MAX_VALUE else COLLAPSED_LINES,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { if (!expanded && it.hasVisualOverflow) overflowing = true },
                modifier = Modifier.padding(top = 12.dp),
            )
            if (overflowing || expanded) {
                val description = stringResource(R.string.detail_read_more_description, review.authorName)
                TextButton(
                    onClick = onToggleExpanded,
                    modifier = Modifier
                        .heightIn(min = 44.dp)
                        .semantics { contentDescription = description },
                ) {
                    Text(
                        text = stringResource(
                            if (expanded) R.string.detail_show_less else R.string.detail_read_more,
                        ),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewRating(rating: Double?) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            if (rating != null) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp),
                )
            }
            Text(
                text = rating?.toRatingLabel() ?: stringResource(R.string.detail_review_no_rating),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = if (rating != null) 4.dp else 0.dp),
            )
        }
    }
}

/** Not an error: a movie with no reviews is a normal result. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NoReviews(modifier: Modifier = Modifier) {
    val dashColor = MaterialTheme.colorScheme.outlineVariant
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(28.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())),
                    ),
                )
            }
            .padding(24.dp),
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(MaterialShapes.Cookie6Sided.toShape())
                .background(MaterialTheme.colorScheme.secondaryContainer),
        )
        Text(
            text = stringResource(R.string.detail_reviews_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.detail_reviews_empty_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewsSectionPreview() {
    MoviedbTheme(paletteId = "violet") {
        ReviewsSection(
            state = SectionState.Content(sampleReviews),
            expandedIds = emptySet(),
            onToggleExpanded = {},
            onRetry = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NoReviewsPreview() {
    MoviedbTheme(paletteId = "violet") {
        ReviewsSection(
            state = SectionState.Empty,
            expandedIds = emptySet(),
            onToggleExpanded = {},
            onRetry = {},
        )
    }
}
