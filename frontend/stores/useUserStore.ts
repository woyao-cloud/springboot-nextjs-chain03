import { create } from 'zustand';
import { User, PagedResponse, ApiError } from '@/types';
import { userApi, GetUsersParams } from '@/lib/api/userApi';

export interface UserState {
  // Data
  users: User[];
  selectedUser: User | null;

  // Pagination
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;

  // Loading & Error states
  isLoading: boolean;
  isLoadingMore: boolean;
  error: ApiError | null;

  // Actions
  fetchUsers: (params?: GetUsersParams) => Promise<void>;
  fetchNextPage: () => Promise<void>;
  fetchPreviousPage: () => Promise<void>;
  setPage: (page: number) => void;
  setPageSize: (size: number) => void;
  selectUser: (user: User | null) => void;
  clearError: () => void;
  refreshUsers: () => Promise<void>;

  // Mutations
  deleteUser: (id: string) => Promise<void>;
  activateUser: (id: string) => Promise<void>;
  deactivateUser: (id: string) => Promise<void>;
}

export const useUserStore = create<UserState>((set, get) => ({
  // Initial state
  users: [],
  selectedUser: null,

  page: 0,
  size: 20,
  totalElements: 0,
  totalPages: 0,
  isFirst: true,
  isLast: false,

  isLoading: false,
  isLoadingMore: false,
  error: null,

  // Fetch users with pagination
  fetchUsers: async (params?: GetUsersParams) => {
    set({ isLoading: true, error: null });

    try {
      const currentState = get();
      const response = await userApi.getUsers({
        page: params?.page ?? currentState.page,
        size: params?.size ?? currentState.size,
        sortBy: params?.sortBy,
        sortDirection: params?.sortDirection ?? 'desc',
      });

      const { data } = response;

      set({
        users: data.content,
        page: data.page,
        size: data.size,
        totalElements: data.totalElements,
        totalPages: data.totalPages,
        isFirst: data.first,
        isLast: data.last,
        isLoading: false,
        error: null,
      });
    } catch (err) {
      const error = err as { response?: { data?: ApiError } };
      set({
        isLoading: false,
        error: error.response?.data || { code: 'FETCH_ERROR', message: 'Failed to fetch users' },
      });
    }
  },

  // Fetch next page
  fetchNextPage: async () => {
    const { page, totalPages, isLast, fetchUsers } = get();
    if (isLast || page >= totalPages - 1) return;

    set({ isLoadingMore: true });
    await fetchUsers({ page: page + 1 });
    set({ isLoadingMore: false });
  },

  // Fetch previous page
  fetchPreviousPage: async () => {
    const { page, isFirst, fetchUsers } = get();
    if (isFirst || page <= 0) return;

    set({ isLoadingMore: true });
    await fetchUsers({ page: page - 1 });
    set({ isLoadingMore: false });
  },

  // Set page
  setPage: (page: number) => {
    set({ page });
    get().fetchUsers({ page });
  },

  // Set page size
  setPageSize: (size: number) => {
    set({ size, page: 0 });
    get().fetchUsers({ page: 0, size });
  },

  // Select user
  selectUser: (user: User | null) => {
    set({ selectedUser: user });
  },

  // Clear error
  clearError: () => {
    set({ error: null });
  },

  // Refresh users (refetch current page)
  refreshUsers: async () => {
    const { page, size } = get();
    await get().fetchUsers({ page, size });
  },

  // Delete user
  deleteUser: async (id: string) => {
    try {
      await userApi.deleteUser(id);
      // Remove from local state
      set((state) => ({
        users: state.users.filter((user) => user.id !== id),
        totalElements: state.totalElements - 1,
      }));
    } catch (err) {
      const error = err as { response?: { data?: ApiError } };
      set({
        error: error.response?.data || { code: 'DELETE_ERROR', message: 'Failed to delete user' },
      });
      throw err;
    }
  },

  // Activate user
  activateUser: async (id: string) => {
    try {
      await userApi.activateUser(id);
      // Update local state
      set((state) => ({
        users: state.users.map((user) =>
          user.id === id ? { ...user, isActive: true } : user
        ),
      }));
    } catch (err) {
      const error = err as { response?: { data?: ApiError } };
      set({
        error: error.response?.data || { code: 'ACTIVATE_ERROR', message: 'Failed to activate user' },
      });
      throw err;
    }
  },

  // Deactivate user
  deactivateUser: async (id: string) => {
    try {
      await userApi.deactivateUser(id);
      // Update local state
      set((state) => ({
        users: state.users.map((user) =>
          user.id === id ? { ...user, isActive: false } : user
        ),
      }));
    } catch (err) {
      const error = err as { response?: { data?: ApiError } };
      set({
        error: error.response?.data || { code: 'DEACTIVATE_ERROR', message: 'Failed to deactivate user' },
      });
      throw err;
    }
  },
}));
