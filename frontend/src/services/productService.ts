import api from './api';
import type { ProductDetail, ProductSummary, PageResponse } from '../types/product';

export const getProducts = (page = 0, size = 12) =>
  api.get<PageResponse<ProductSummary>>(`/productos?page=${page}&size=${size}`);

export const getProductById = (id: number) =>
  api.get<ProductDetail>(`/productos/${id}`);

export const searchProducts = (query: string, page = 0) =>
  api.get<PageResponse<ProductSummary>>(`/productos/buscar?q=${query}&page=${page}`);

export const getLowStock = (umbral = 5) =>
  api.get<ProductSummary[]>(`/productos/stock-bajo?umbral=${umbral}`);

export const updateStock = (id: number, cantidad: number, operacion: 'INCREMENTAR' | 'REDUCIR') =>
  api.put<ProductDetail>(`/productos/${id}/stock`, { cantidad, operacion });