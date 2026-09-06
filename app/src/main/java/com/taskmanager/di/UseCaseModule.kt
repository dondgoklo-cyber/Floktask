package com.taskmanager.di

import com.taskmanager.domain.usecase.activity.RecordTaskChangeUseCase
import com.taskmanager.domain.usecase.batch.BatchTaskOperationsUseCase
import com.taskmanager.domain.usecase.conflict.DetectConflictsUseCase
import com.taskmanager.domain.usecase.customfield.ValidateCustomFieldValueUseCase
import com.taskmanager.domain.usecase.deps.TaskDependencyUseCases
import com.taskmanager.domain.usecase.eisenhower.GetEisenhowerTasksUseCase
import com.taskmanager.domain.usecase.eisenhower.UpdateEisenhowerQuadrantUseCase
import com.taskmanager.domain.usecase.energy.GetEnergyProfileUseCase
import com.taskmanager.domain.usecase.finance.CreateAccountUseCase
import com.taskmanager.domain.usecase.finance.CreateBudgetUseCase
import com.taskmanager.domain.usecase.finance.CreateCategoryUseCase
import com.taskmanager.domain.usecase.finance.CreateGoalUseCase
import com.taskmanager.domain.usecase.finance.CreateTransactionUseCase
import com.taskmanager.domain.usecase.finance.DeleteAccountUseCase
import com.taskmanager.domain.usecase.finance.DeleteBudgetUseCase
import com.taskmanager.domain.usecase.finance.DeleteCategoryUseCase
import com.taskmanager.domain.usecase.finance.DeleteGoalUseCase
import com.taskmanager.domain.usecase.finance.DeleteTransactionUseCase
import com.taskmanager.domain.usecase.finance.GetAccountsUseCase
import com.taskmanager.domain.usecase.finance.GetAllBudgetsUseCase
import com.taskmanager.domain.usecase.finance.GetAllGoalsUseCase
import com.taskmanager.domain.usecase.finance.GetAllTransactionsUseCase
import com.taskmanager.domain.usecase.finance.GetCategoriesUseCase
import com.taskmanager.domain.usecase.finance.GetFinanceSummaryUseCase
import com.taskmanager.domain.usecase.finance.GetRecentTransactionsUseCase
import com.taskmanager.domain.usecase.finance.UpdateCategoryUseCase
import com.taskmanager.domain.usecase.finance.UpdateTransactionUseCase
import com.taskmanager.domain.usecase.finance.UpsertBudgetUseCase
import com.taskmanager.domain.usecase.gamification.ObserveUserStatsUseCase
import com.taskmanager.domain.usecase.gamification.RecordTaskCompletionUseCase
import com.taskmanager.domain.usecase.habit.CreateHabitUseCase
import com.taskmanager.domain.usecase.habit.GetActiveHabitsUseCase
import com.taskmanager.domain.usecase.habit.GetAllHabitsUseCase
import com.taskmanager.domain.usecase.habit.GetHabitStatsUseCase
import com.taskmanager.domain.usecase.habit.LogHabitCompletionUseCase
import com.taskmanager.domain.usecase.note.CreateFolderUseCase
import com.taskmanager.domain.usecase.note.CreateNoteUseCase
import com.taskmanager.domain.usecase.note.DeleteNoteUseCase
import com.taskmanager.domain.usecase.note.GetAllFoldersUseCase
import com.taskmanager.domain.usecase.note.GetAllNotesUseCase
import com.taskmanager.domain.usecase.note.GetNoteByIdUseCase
import com.taskmanager.domain.usecase.note.GetNotesByProjectUseCase
import com.taskmanager.domain.usecase.note.SearchNotesUseCase
import com.taskmanager.domain.usecase.note.SetPinnedUseCase
import com.taskmanager.domain.usecase.note.UpdateNoteUseCase
import com.taskmanager.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import com.taskmanager.domain.usecase.pomodoro.SavePomodoroSessionUseCase
import com.taskmanager.domain.usecase.project.CreateProjectUseCase
import com.taskmanager.domain.usecase.project.GetAllProjectsFlowUseCase
import com.taskmanager.domain.usecase.project.GetAllProjectsUseCase
import com.taskmanager.domain.usecase.project.GetProjectByIdUseCase
import com.taskmanager.domain.usecase.project.GetProjectNameByIdUseCase
import com.taskmanager.domain.usecase.recurrence.RecurrenceScheduler
import com.taskmanager.domain.usecase.schedule.AutoScheduleTasksUseCase
import com.taskmanager.domain.usecase.search.GlobalSearchUseCase
import com.taskmanager.domain.usecase.settings.GetBaseCurrencyUseCase
import com.taskmanager.domain.usecase.subtask.CreateSubtaskUseCase
import com.taskmanager.domain.usecase.subtask.DeleteSubtaskUseCase
import com.taskmanager.domain.usecase.subtask.GetSubtaskTreeUseCase
import com.taskmanager.domain.usecase.subtask.ReorderSubtasksUseCase
import com.taskmanager.domain.usecase.subtask.SetSubtaskCompletedUseCase
import com.taskmanager.domain.usecase.subtask.UpdateSubtaskUseCase
import com.taskmanager.domain.usecase.tag.CreateTagUseCase
import com.taskmanager.domain.usecase.tag.DeleteTagUseCase
import com.taskmanager.domain.usecase.tag.GetAllTagsUseCase
import com.taskmanager.domain.usecase.tag.UpdateTagUseCase
import com.taskmanager.domain.usecase.task.CreateTaskUseCase
import com.taskmanager.domain.usecase.task.DeleteTaskUseCase
import com.taskmanager.domain.usecase.task.GetAllTasksUseCase
import com.taskmanager.domain.usecase.task.GetInboxTasksUseCase
import com.taskmanager.domain.usecase.task.GetTaskByIdUseCase
import com.taskmanager.domain.usecase.task.GetTasksByProjectUseCase
import com.taskmanager.domain.usecase.task.GetUpcomingTasksUseCase
import com.taskmanager.domain.usecase.task.SearchTasksUseCase
import com.taskmanager.domain.usecase.task.SetTaskCompletedUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskStatusUseCase
import com.taskmanager.domain.usecase.task.UpdateTaskUseCase
import com.taskmanager.domain.usecase.timetracking.GetTaskTimeSummaryUseCase
import com.taskmanager.domain.usecase.validation.TaskValidator
import com.taskmanager.domain.usecase.validator.ValidateCustomFieldValueUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module that binds all use cases to their implementations.
 * This ensures that all use cases with @Inject constructors are available
 * for dependency injection throughout the application.
 * 
 * Part of Clean Architecture - UseCases are the interface between Domain and Presentation layers.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    // Activity use cases
    @Binds
    abstract fun bindRecordTaskChangeUseCase(impl: RecordTaskChangeUseCase): RecordTaskChangeUseCase

    // Batch operations
    @Binds
    abstract fun bindBatchTaskOperationsUseCase(impl: BatchTaskOperationsUseCase): BatchTaskOperationsUseCase

    // Conflict detection
    @Binds
    abstract fun bindDetectConflictsUseCase(impl: DetectConflictsUseCase): DetectConflictsUseCase

    // Custom field validation
    @Binds
    abstract fun bindValidateCustomFieldValueUseCase(impl: ValidateCustomFieldValueUseCase): ValidateCustomFieldValueUseCase

    // Task dependencies
    @Binds
    abstract fun bindTaskDependencyUseCases(impl: TaskDependencyUseCases): TaskDependencyUseCases

    // Eisenhower
    @Binds
    abstract fun bindGetEisenhowerTasksUseCase(impl: GetEisenhowerTasksUseCase): GetEisenhowerTasksUseCase

    @Binds
    abstract fun bindUpdateEisenhowerQuadrantUseCase(impl: UpdateEisenhowerQuadrantUseCase): UpdateEisenhowerQuadrantUseCase

    // Energy
    @Binds
    abstract fun bindGetEnergyProfileUseCase(impl: GetEnergyProfileUseCase): GetEnergyProfileUseCase

    // Finance
    @Binds
    abstract fun bindCreateAccountUseCase(impl: CreateAccountUseCase): CreateAccountUseCase

    @Binds
    abstract fun bindCreateBudgetUseCase(impl: CreateBudgetUseCase): CreateBudgetUseCase

    @Binds
    abstract fun bindCreateCategoryUseCase(impl: CreateCategoryUseCase): CreateCategoryUseCase

    @Binds
    abstract fun bindCreateGoalUseCase(impl: CreateGoalUseCase): CreateGoalUseCase

    @Binds
    abstract fun bindCreateTransactionUseCase(impl: CreateTransactionUseCase): CreateTransactionUseCase

    @Binds
    abstract fun bindDeleteAccountUseCase(impl: DeleteAccountUseCase): DeleteAccountUseCase

    @Binds
    abstract fun bindDeleteBudgetUseCase(impl: DeleteBudgetUseCase): DeleteBudgetUseCase

    @Binds
    abstract fun bindDeleteCategoryUseCase(impl: DeleteCategoryUseCase): DeleteCategoryUseCase

    @Binds
    abstract fun bindDeleteGoalUseCase(impl: DeleteGoalUseCase): DeleteGoalUseCase

    @Binds
    abstract fun bindDeleteTransactionUseCase(impl: DeleteTransactionUseCase): DeleteTransactionUseCase

    @Binds
    abstract fun bindGetAccountsUseCase(impl: GetAccountsUseCase): GetAccountsUseCase

    @Binds
    abstract fun bindGetAllBudgetsUseCase(impl: GetAllBudgetsUseCase): GetAllBudgetsUseCase

    @Binds
    abstract fun bindGetAllGoalsUseCase(impl: GetAllGoalsUseCase): GetAllGoalsUseCase

    @Binds
    abstract fun bindGetAllTransactionsUseCase(impl: GetAllTransactionsUseCase): GetAllTransactionsUseCase

    @Binds
    abstract fun bindGetCategoriesUseCase(impl: GetCategoriesUseCase): GetCategoriesUseCase

    @Binds
    abstract fun bindGetFinanceSummaryUseCase(impl: GetFinanceSummaryUseCase): GetFinanceSummaryUseCase

    @Binds
    abstract fun bindGetRecentTransactionsUseCase(impl: GetRecentTransactionsUseCase): GetRecentTransactionsUseCase

    @Binds
    abstract fun bindUpdateCategoryUseCase(impl: UpdateCategoryUseCase): UpdateCategoryUseCase

    @Binds
    abstract fun bindUpdateTransactionUseCase(impl: UpdateTransactionUseCase): UpdateTransactionUseCase

    @Binds
    abstract fun bindUpsertBudgetUseCase(impl: UpsertBudgetUseCase): UpsertBudgetUseCase

    // Gamification
    @Binds
    abstract fun bindObserveUserStatsUseCase(impl: ObserveUserStatsUseCase): ObserveUserStatsUseCase

    @Binds
    abstract fun bindRecordTaskCompletionUseCase(impl: RecordTaskCompletionUseCase): RecordTaskCompletionUseCase

    // Habit
    @Binds
    abstract fun bindCreateHabitUseCase(impl: CreateHabitUseCase): CreateHabitUseCase

    @Binds
    abstract fun bindGetActiveHabitsUseCase(impl: GetActiveHabitsUseCase): GetActiveHabitsUseCase

    @Binds
    abstract fun bindGetAllHabitsUseCase(impl: GetAllHabitsUseCase): GetAllHabitsUseCase

    @Binds
    abstract fun bindGetHabitStatsUseCase(impl: GetHabitStatsUseCase): GetHabitStatsUseCase

    @Binds
    abstract fun bindLogHabitCompletionUseCase(impl: LogHabitCompletionUseCase): LogHabitCompletionUseCase

    // Note
    @Binds
    abstract fun bindCreateFolderUseCase(impl: CreateFolderUseCase): CreateFolderUseCase

    @Binds
    abstract fun bindCreateNoteUseCase(impl: CreateNoteUseCase): CreateNoteUseCase

    @Binds
    abstract fun bindDeleteNoteUseCase(impl: DeleteNoteUseCase): DeleteNoteUseCase

    @Binds
    abstract fun bindGetAllFoldersUseCase(impl: GetAllFoldersUseCase): GetAllFoldersUseCase

    @Binds
    abstract fun bindGetAllNotesUseCase(impl: GetAllNotesUseCase): GetAllNotesUseCase

    @Binds
    abstract fun bindGetNoteByIdUseCase(impl: GetNoteByIdUseCase): GetNoteByIdUseCase

    @Binds
    abstract fun bindGetNotesByProjectUseCase(impl: GetNotesByProjectUseCase): GetNotesByProjectUseCase

    @Binds
    abstract fun bindSearchNotesUseCase(impl: SearchNotesUseCase): SearchNotesUseCase

    @Binds
    abstract fun bindSetPinnedUseCase(impl: SetPinnedUseCase): SetPinnedUseCase

    @Binds
    abstract fun bindUpdateNoteUseCase(impl: UpdateNoteUseCase): UpdateNoteUseCase

    // Pomodoro
    @Binds
    abstract fun bindGetPomodoroStatsUseCase(impl: GetPomodoroStatsUseCase): GetPomodoroStatsUseCase

    @Binds
    abstract fun bindSavePomodoroSessionUseCase(impl: SavePomodoroSessionUseCase): SavePomodoroSessionUseCase

    // Project
    @Binds
    abstract fun bindCreateProjectUseCase(impl: CreateProjectUseCase): CreateProjectUseCase

    @Binds
    abstract fun bindGetAllProjectsFlowUseCase(impl: GetAllProjectsFlowUseCase): GetAllProjectsFlowUseCase

    @Binds
    abstract fun bindGetAllProjectsUseCase(impl: GetAllProjectsUseCase): GetAllProjectsUseCase

    @Binds
    abstract fun bindGetProjectByIdUseCase(impl: GetProjectByIdUseCase): GetProjectByIdUseCase

    @Binds
    abstract fun bindGetProjectNameByIdUseCase(impl: GetProjectNameByIdUseCase): GetProjectNameByIdUseCase

    // Recurrence
    @Binds
    abstract fun bindRecurrenceScheduler(impl: RecurrenceScheduler): RecurrenceScheduler

    // Schedule
    @Binds
    abstract fun bindAutoScheduleTasksUseCase(impl: AutoScheduleTasksUseCase): AutoScheduleTasksUseCase

    // Search
    @Binds
    abstract fun bindGlobalSearchUseCase(impl: GlobalSearchUseCase): GlobalSearchUseCase

    // Settings
    @Binds
    abstract fun bindGetBaseCurrencyUseCase(impl: GetBaseCurrencyUseCase): GetBaseCurrencyUseCase

    // Subtask
    @Binds
    abstract fun bindCreateSubtaskUseCase(impl: CreateSubtaskUseCase): CreateSubtaskUseCase

    @Binds
    abstract fun bindDeleteSubtaskUseCase(impl: DeleteSubtaskUseCase): DeleteSubtaskUseCase

    @Binds
    abstract fun bindGetSubtaskTreeUseCase(impl: GetSubtaskTreeUseCase): GetSubtaskTreeUseCase

    @Binds
    abstract fun bindReorderSubtasksUseCase(impl: ReorderSubtasksUseCase): ReorderSubtasksUseCase

    @Binds
    abstract fun bindSetSubtaskCompletedUseCase(impl: SetSubtaskCompletedUseCase): SetSubtaskCompletedUseCase

    @Binds
    abstract fun bindUpdateSubtaskUseCase(impl: UpdateSubtaskUseCase): UpdateSubtaskUseCase

    // Tag
    @Binds
    abstract fun bindCreateTagUseCase(impl: CreateTagUseCase): CreateTagUseCase

    @Binds
    abstract fun bindDeleteTagUseCase(impl: DeleteTagUseCase): DeleteTagUseCase

    @Binds
    abstract fun bindGetAllTagsUseCase(impl: GetAllTagsUseCase): GetAllTagsUseCase

    @Binds
    abstract fun bindUpdateTagUseCase(impl: UpdateTagUseCase): UpdateTagUseCase

    // Task
    @Binds
    abstract fun bindCreateTaskUseCase(impl: CreateTaskUseCase): CreateTaskUseCase

    @Binds
    abstract fun bindDeleteTaskUseCase(impl: DeleteTaskUseCase): DeleteTaskUseCase

    @Binds
    abstract fun bindGetAllTasksUseCase(impl: GetAllTasksUseCase): GetAllTasksUseCase

    @Binds
    abstract fun bindGetInboxTasksUseCase(impl: GetInboxTasksUseCase): GetInboxTasksUseCase

    @Binds
    abstract fun bindGetTaskByIdUseCase(impl: GetTaskByIdUseCase): GetTaskByIdUseCase

    @Binds
    abstract fun bindGetTasksByProjectUseCase(impl: GetTasksByProjectUseCase): GetTasksByProjectUseCase

    @Binds
    abstract fun bindGetUpcomingTasksUseCase(impl: GetUpcomingTasksUseCase): GetUpcomingTasksUseCase

    @Binds
    abstract fun bindSearchTasksUseCase(impl: SearchTasksUseCase): SearchTasksUseCase

    @Binds
    abstract fun bindSetTaskCompletedUseCase(impl: SetTaskCompletedUseCase): SetTaskCompletedUseCase

    @Binds
    abstract fun bindUpdateTaskStatusUseCase(impl: UpdateTaskStatusUseCase): UpdateTaskStatusUseCase

    @Binds
    abstract fun bindUpdateTaskUseCase(impl: UpdateTaskUseCase): UpdateTaskUseCase

    // Time tracking
    @Binds
    abstract fun bindGetTaskTimeSummaryUseCase(impl: GetTaskTimeSummaryUseCase): GetTaskTimeSummaryUseCase

    // Validation
    @Binds
    abstract fun bindTaskValidator(impl: TaskValidator): TaskValidator
}
