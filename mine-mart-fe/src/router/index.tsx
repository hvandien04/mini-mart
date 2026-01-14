/**
 * Router configuration với route guards
 */

import { createBrowserRouter, Navigate } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import LoginPage from '@/pages/login/LoginPage';
import CategoryListPage from '@/pages/categories/CategoryListPage';
import ProductListPage from '@/pages/products/ProductListPage';
import ImportReceiptListPage from '@/pages/imports/ImportReceiptListPage';
import ImportReceiptCreatePage from '@/pages/imports/ImportReceiptCreatePage';
import ImportReceiptDetailPage from '@/pages/imports/ImportReceiptDetailPage';
import ExportReceiptListPage from '@/pages/exports/ExportReceiptListPage';
import ExportReceiptCreatePage from '@/pages/exports/ExportReceiptCreatePage';
import ExportReceiptDetailPage from '@/pages/exports/ExportReceiptDetailPage';
import ReportPage from '@/pages/reports/ReportPage';
import UserListPage from '@/pages/users/UserListPage';
import SupplierListPage from '@/pages/suppliers/SupplierListPage';
import CustomerListPage from '@/pages/customers/CustomerListPage';
import AiAssistantPage from '@/pages/ai/AiAssistantPage';
import Layout from '@/components/layout/Layout';

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/products" replace />,
      },
      {
        path: 'categories',
        element: (
          <ProtectedRoute requireAdmin>
            <CategoryListPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'products',
        element: <ProductListPage />,
      },
      {
        path: 'imports',
        element: <ImportReceiptListPage />,
      },
      {
        path: 'imports/create',
        element: <ImportReceiptCreatePage />,
      },
      {
        path: 'imports/:id',
        element: <ImportReceiptDetailPage />,
      },
      {
        path: 'exports',
        element: <ExportReceiptListPage />,
      },
      {
        path: 'exports/create',
        element: <ExportReceiptCreatePage />,
      },
      {
        path: 'exports/:id',
        element: <ExportReceiptDetailPage />,
      },
      {
        path: 'reports',
        element: <ReportPage />,
      },
      {
        path: 'users',
        element: (
          <ProtectedRoute requireAdmin>
            <UserListPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'suppliers',
        element: (
          <ProtectedRoute requireAdmin>
            <SupplierListPage />
          </ProtectedRoute>
        ),
      },
      {
        path: 'customers',
        element: <CustomerListPage />,
      },
      {
        path: 'ai-assistant',
        element: <AiAssistantPage />,
      },
    ],
  },
]);

