package com.pokiepaws.mobile.ui.animals.animalvisitshistory

import com.pokiepaws.mobile.MainDispatcherRule
import com.pokiepaws.mobile.domain.model.Prescription
import com.pokiepaws.mobile.domain.model.Visit
import com.pokiepaws.mobile.domain.model.VisitDescription
import com.pokiepaws.mobile.domain.repository.VisitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AnimalVisitsHistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val visitRepository = mockk<VisitRepository>()

    @Test
    fun loadVisitsShowsVisitsWithPrescriptionsForSelectedAnimal() =
        runTest {
            val firstVisit = sampleVisit(id = 1L, animalId = 44L)
            val secondVisit = sampleVisit(id = 2L, animalId = 44L)
            val prescription = samplePrescription(visitId = firstVisit.id)
            every { visitRepository.getByAnimal(44L) } returns flowOf(listOf(firstVisit, secondVisit))
            every { visitRepository.observePrescription(firstVisit.id) } returns flowOf(prescription)
            every { visitRepository.observePrescription(secondVisit.id) } returns flowOf(null)
            coEvery { visitRepository.syncByAnimal(44L) } returns Unit
            coEvery { visitRepository.syncPrescription(any()) } returns Unit
            val viewModel = AnimalVisitsHistoryViewModel(visitRepository)

            viewModel.loadVisits(44L)

            val state = viewModel.uiState.value as AnimalVisitsHistoryUiState.Success
            assertEquals(2, state.items.size)
            assertEquals(firstVisit, state.items[0].visit)
            assertEquals(prescription, state.items[0].prescription)
            assertEquals(secondVisit, state.items[1].visit)
            assertEquals(null, state.items[1].prescription)
            coVerify { visitRepository.syncByAnimal(44L) }
            coVerify { visitRepository.syncPrescription(firstVisit.id) }
            coVerify { visitRepository.syncPrescription(secondVisit.id) }
        }

    @Test
    fun loadVisitsShowsEmptyHistoryWhenAnimalHasNoVisits() =
        runTest {
            every { visitRepository.getByAnimal(44L) } returns flowOf(emptyList())
            coEvery { visitRepository.syncByAnimal(44L) } returns Unit
            val viewModel = AnimalVisitsHistoryViewModel(visitRepository)

            viewModel.loadVisits(44L)

            val state = viewModel.uiState.value as AnimalVisitsHistoryUiState.Success
            assertEquals(emptyList<AnimalVisitHistoryItem>(), state.items)
        }
}

private fun sampleVisit(
    id: Long,
    animalId: Long,
) = Visit(
    id = id,
    animalId = animalId,
    clinicId = 10L,
    vetUserId = 20L,
    startsAt = "2026-07-01T10:00:00",
    endsAt = "2026-07-01T10:30:00",
    description = VisitDescription.VACCINATION,
    disease = null,
    diagnosis = "Szczepienie",
    recommendations = "Kontrola za rok",
    status = "COMPLETED",
)

private fun samplePrescription(visitId: Long) =
    Prescription(
        id = 1L,
        visitId = visitId,
        vetUserId = 20L,
        clinicId = 10L,
        recommendationDate = "2026-07-01",
        creationDate = "2026-07-01",
        items = emptyList(),
    )
