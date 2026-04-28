import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductById, updateStock } from '../services/productService';
import { useAuth } from '../context/AuthContext';
import type { ProductDetail as ProductDetailType } from '../types/product';
import { ArrowLeft, Edit3, Package } from 'lucide-react';
import toast from 'react-hot-toast';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { hasRole } = useAuth();
  const [product, setProduct] = useState<ProductDetailType | null>(null);
  const [stock, setStock] = useState(0);
  const [updating, setUpdating] = useState(false);

  useEffect(() => {
    if (id) {
      getProductById(Number(id)).then(res => {
        setProduct(res.data);
        setStock(res.data.stock);
      });
    }
  }, [id]);

  const handleStockUpdate = async () => {
    if (!product) return;
    const diferencia = stock - product.stock;
    const operacion = diferencia > 0 ? 'INCREMENTAR' : 'REDUCIR';
    const cantidad = Math.abs(diferencia);
    setUpdating(true);
    try {
        await updateStock(product.id, cantidad, operacion);
        toast.success('Stock actualizado');
        setProduct({ ...product, stock });
    } catch (error) {
        toast.error('Error al actualizar stock');
    } finally {
        setUpdating(false);
    }
    };

  if (!product) return <div className="flex justify-center py-12">Cargando...</div>;

  return (
    <div className="max-w-4xl mx-auto">
      <button onClick={() => navigate('/')} className="mb-6 flex items-center text-indigo-600 hover:text-indigo-800">
        <ArrowLeft className="h-5 w-5 mr-1" /> Volver
      </button>
      <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-xl overflow-hidden">
        <div className="p-8">
          <div className="flex items-center space-x-3 mb-4">
            <Package className="h-10 w-10 text-indigo-500" />
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">{product.nombre}</h1>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="space-y-4">
              <div><span className="font-semibold">SKU:</span> {product.sku}</div>
              <div><span className="font-semibold">Categoría:</span> {product.categoriaNombre}</div>
              <div><span className="font-semibold">Estado:</span> <span className={`px-2 py-1 rounded-full text-xs ${product.estado === 'ACTIVO' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>{product.estado}</span></div>
              <div><span className="font-semibold">Descripción:</span> {product.descripcion}</div>
              <div><span className="font-semibold">Fechas:</span> Creado: {new Date(product.createdAt).toLocaleDateString()} - Actualizado: {new Date(product.updatedAt).toLocaleDateString()}</div>
            </div>
            <div className="space-y-4">
              <div className="text-4xl font-bold text-indigo-600">${product.precio.toLocaleString()}</div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300">Stock actual</label>
                <div className="mt-1 flex items-center space-x-2">
                  <input type="number" value={stock} onChange={(e) => setStock(Number(e.target.value))} className="block w-32 rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 dark:bg-gray-700" disabled={!hasRole('INVENTARIO')} />
                  {hasRole('INVENTARIO') && (
                    <button onClick={handleStockUpdate} disabled={updating} className="px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 disabled:opacity-50">
                      {updating ? 'Actualizando...' : 'Actualizar stock'}
                    </button>
                  )}
                </div>
              </div>
              <div className="pt-4">
                {hasRole('PRODUCTOS') && (
                  <button className="w-full flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-yellow-600 hover:bg-yellow-700">
                    <Edit3 className="h-5 w-5 mr-2" /> Editar producto (próximamente)
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
