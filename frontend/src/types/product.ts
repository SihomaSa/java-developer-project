export interface ProductSummary {
  id: number;
  nombre: string;
  sku: string;
  precio: number;
  stock: number;
  estado: 'ACTIVO' | 'INACTIVO' | 'AGOTADO' | 'DESCONTINUADO';
}

export interface ProductDetail extends ProductSummary {
  descripcion: string;
  categoriaNombre: string;
  tieneStock: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}