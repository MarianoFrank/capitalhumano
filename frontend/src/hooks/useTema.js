import { useContext } from 'react';
import { TemaContext } from '../context/TemaContext';

export const useTema = () => useContext(TemaContext);
export const useTheme = useTema;

export default useTema;
